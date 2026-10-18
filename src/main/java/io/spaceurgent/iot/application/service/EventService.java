package io.spaceurgent.iot.application.service;

import io.spaceurgent.iot.application.dto.EventType;
import io.spaceurgent.iot.application.dto.NewEventDto;
import io.spaceurgent.iot.application.task.CheckPowerChangesTask;
import io.spaceurgent.iot.domain.model.PowerEvent;
import io.spaceurgent.iot.domain.repository.PowerEventRepository;
import io.spaceurgent.iot.infrastructure.config.VirtualThreadExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.ExecutorService;

@Slf4j
@ApplicationScoped
public class EventService {
    private final ExecutorService executorService;
    private final PowerEventRepository powerEventRepository;
    private final CheckPowerChangesTask checkPowerChangesTask;

    @Inject
    public EventService(@VirtualThreadExecutor ExecutorService executorService,
                         PowerEventRepository powerEventRepository,
                         CheckPowerChangesTask checkPowerChangesTask) {
        this.executorService = executorService;
        this.powerEventRepository = powerEventRepository;
        this.checkPowerChangesTask = checkPowerChangesTask;
    }

    @Transactional
    public void handleEvent(NewEventDto eventDto) {
        EventType eventType = eventDto.eventType();
        if (EventType.POWER_STATUS != eventType) {
            log.warn("Notification event type {} not supported", eventType);
            return;
        }
        PowerEvent powerEvent = PowerEvent.builder()
                .createdAt(Instant.now())
                .timestamp(eventDto.timestamp())
                .powerPresent(eventDto.powerPresent())
                .build();
        powerEventRepository.persist(powerEvent);
        PowerEvent finalPowerEvent = powerEvent;
        executorService.execute(() -> checkPowerChangesTask.run(finalPowerEvent));
    }
}
