package io.spaceurgent.iot.application.task;

import io.spaceurgent.iot.application.interfaces.NotificationGateway;
import io.spaceurgent.iot.application.interfaces.NotificationTextFactory;
import io.spaceurgent.iot.domain.model.Notification;
import io.spaceurgent.iot.domain.model.PowerEvent;
import io.spaceurgent.iot.domain.repository.NotificationRepository;
import io.spaceurgent.iot.domain.repository.PowerEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckPowerChangesTask {
    private final NotificationTextFactory notificationTextFactory;
    private final NotificationGateway notificationGateway;
    private final PowerEventRepository powerEventRepository;
    private final NotificationRepository notificationRepository;

    public void run(PowerEvent powerEvent) {
        if (powerEvent == null) {
            log.warn("power event is null");
            return;
        }
        powerEventRepository.findPreviousById(powerEvent.getId()).ifPresent(previousPowerEvent -> {
            checkPowerChange(powerEvent, previousPowerEvent);
        });
    }

    private void checkPowerChange(PowerEvent lastEvent, PowerEvent previousEvent) {
        if (Objects.equals(lastEvent.getPowerPresent(), previousEvent.getPowerPresent())) {
            log.debug("power has not changed");
            return;
        }
        Notification notification = lastEvent.getPowerPresent()
                ? createPowerOnNotification(lastEvent, previousEvent)
                : createPowerOffNotification(lastEvent);
        notification = notificationGateway.send(notification);
        notificationRepository.save(notification);
    }

    private Notification createPowerOnNotification(PowerEvent lastEvent, PowerEvent previousEvent) {
        return Notification.builder()
                .tag(Notification.Tags.POWER_ON)
                .text(notificationTextFactory.powerOnNotification(lastEvent.getTimestamp(), previousEvent.getTimestamp()))
                .build();
    }

    private Notification createPowerOffNotification(PowerEvent lastEvent) {
        return Notification.builder()
                .createdAt(Instant.now())
                .tag(Notification.Tags.POWER_OFF)
                .text(notificationTextFactory.powerOffNotification(lastEvent.getTimestamp()))
                .build();
    }
}
