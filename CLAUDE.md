# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this service does

`iot-service` is a Quarkus API that receives power-status events from an IoT device (e.g. an
ESP32 monitoring mains power) and sends Telegram notifications in Ukrainian when power goes off or
comes back on, including how long the outage lasted.

Flow: device `POST`s to `/api/v1/events` → `EventService` persists a `PowerEvent` → the event is
compared against the previous one asynchronously → if the `powerPresent` state changed,
`CheckPowerChangesTask` builds and sends a Telegram message via `TelegramNotificationGateway`, then
records the outcome as a `Notification`.

## Commands

Build and run with Maven (`mvn`). Note: the checked-in `mvnw`/`mvnw.cmd` wrapper scripts have no
matching `.mvn/wrapper/maven-wrapper.properties`, so they currently fail — use a system `mvn`
(3.9+) until the wrapper is regenerated (`mvn -N io.takari:maven:wrapper` or
`quarkus create --wrapper`).

- Build: `mvn clean package`
- Run all tests: `mvn test`
- Run a single test: `mvn test -Dtest=IotApplicationTests#contextLoads`
- Run the app in dev mode (needs Postgres on `localhost:5433`, see below):
  `mvn quarkus:dev -Dquarkus.profile=local`
- Build the container image: `docker compose build` — the Dockerfile is runtime-only and copies a
  pre-built `target/quarkus-app/`, so run `mvn clean package` first (or unzip a shipped
  `quarkus-app.zip`, which contains `target/quarkus-app/`, next to the Dockerfile on the server)
- Run via Docker: `docker compose up` (requires an external Docker network named `iot-network`:
  `docker network create iot-network`, plus the env vars listed in `docker-compose.yml`)

Tests boot the full Quarkus app (`@QuarkusTest`). With no datasource URL configured, Quarkus Dev
Services will try to auto-start a Postgres container via Testcontainers, which requires a running
Docker daemon.

### Profiles

Quarkus profile files sit alongside `application.yaml` and are picked up automatically
(`quarkus-config-yaml`); activate with `-Dquarkus.profile=<name>` or the `QUARKUS_PROFILE` env var.

- `local` (`application-local.yml`) — hardcodes local Postgres credentials and a real Telegram bot
  token/chat id for manual testing. This file matches `*-local.yml` in `.gitignore` and is not
  committed; treat any copy of it as containing live secrets.
- `docker` (`application-docker.yml`) — every value comes from environment variables
  (`CLIENT_NAME`, `CLIENT_PASSWORD`, `DB_URL`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`,
  `TG_BOT_BASE_URL`, `TG_BOT_TOKEN`, `TG_CHAT_ID`), matching `docker-compose.yml`.

## Architecture

Package layout under `io.spaceurgent.iot` follows a light hexagonal/layered style. Beans are CDI
(`@ApplicationScoped`), most use Lombok `@RequiredArgsConstructor(onConstructor_ = @Inject)` for
constructor injection — except where a bean needs a CDI qualifier on just one constructor
parameter (Lombok's `onConstructor_` stamps annotations on the constructor as a whole, not
per-parameter), in which case the constructor is written by hand (see `EventService`).

- `api` — REST resources (Quarkus REST / JAX-RS). Currently just `EventNotificationController`
  (`POST /api/v1/events`), which delegates straight to `EventService`.
- `application` — use-case orchestration.
  - `service/EventService` — validates the incoming `EventType` (only `POWER_STATUS` is handled
    today; `BATTERY_LEVEL` is defined but not implemented), persists a `PowerEvent`, then hands off
    to `CheckPowerChangesTask` on a background executor.
  - `task/CheckPowerChangesTask` — looks up the previous `PowerEvent` and, if `powerPresent`
    flipped, builds a `Notification` via `NotificationTextFactory` and sends it via
    `NotificationGateway`. Annotated `@Transactional` itself (rather than relying on an inherited
    transaction) because it runs on a raw executor thread with no request/transaction context of
    its own — see the note below.
  - `interfaces` — `NotificationGateway` and `NotificationTextFactory` are the ports the
    application layer depends on; `infrastructure.telegram` is the only current adapter.
  - `dto` — request-facing types (`NewEventDto`, `EventType`).
- `domain` — JPA entities (`PowerEvent`, `Notification`) and Panache repositories
  (`PanacheRepository<T>`). `PowerEventRepository.findPreviousById` is the key query: it fetches
  the chronologically previous event by `id < :id`, ordered by `timestamp desc`, which is how
  power-state transitions are detected.
- `infrastructure` — adapters and cross-cutting config.
  - `telegram/` — `TelegramRestClient` is a MicroProfile REST Client interface pointed at the bot's
    base URL + token via `quarkus.rest-client.telegram-api.url` (composed from the `telegram.*`
    config in `application*.yml`); `TelegramConfig` is a `@ConfigMapping(prefix = "telegram")`
    interface; `TelegramNotificationGateway` implements `NotificationGateway` and maps
    success/failure into `Notification.Status.SENT`/`ERROR`; `TelegramNotificationTextFactory`
    implements `NotificationTextFactory` and owns the Ukrainian-language message templates (uses
    `TimeUtils.toKyivTime` for display).
  - `config/VirtualThreadConfig` — CDI `@Produces` bean for the `ExecutorService`
    (`Executors.newVirtualThreadPerTaskExecutor()`) that `EventService` uses to run
    `CheckPowerChangesTask` off the request thread. Qualified with the custom
    `@VirtualThreadExecutor` annotation because Quarkus's built-in worker-pool executor is also an
    unqualified `ExecutorService` bean and would otherwise collide (`AmbiguousResolutionException`).
  - `security/` — `IotUserIdentityProvider` is a custom `IdentityProvider<UsernamePasswordAuthenticationRequest>`
    checking the single device credential pair from `IotSecurityConfig` (`iot.security.username`/
    `password`, sourced from `CLIENT_NAME`/`CLIENT_PASSWORD` in the `docker` profile). HTTP Basic and
    a default authenticated-everywhere policy are enabled via `quarkus.http.auth.*` in
    `application.yaml` (no dedicated Spring-style filter-chain class — this is the Quarkus-idiomatic
    equivalent of the old `SecurityConfig`).
- `shared/TimeUtils` — Kyiv-timezone conversion helper used for notification text.

Notes for future changes:
- `application/service/NotificationService` and `application/NotificationTemplate` are present but
  empty/unused stubs (plain classes, no CDI annotations) — don't assume they're wired into the flow.
- The service targets Java 25.
- `CheckPowerChangesTask` runs on the virtual-thread executor, invoked from inside
  `EventService.handleEvent`'s own `@Transactional` method, so its lookup can race with the
  enclosing transaction's commit — be aware of this if working on event ordering or notification
  correctness. It has its own `@Transactional` only to get a JTA transaction/Hibernate session
  bound on that background thread (unlike Spring Data, Panache does not implicitly wrap every
  repository call in its own transaction).
