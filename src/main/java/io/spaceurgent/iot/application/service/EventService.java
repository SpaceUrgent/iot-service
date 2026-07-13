package io.spaceurgent.iot.application.service;

import io.spaceurgent.iot.application.dto.EventType;
import io.spaceurgent.iot.application.dto.NewEventDto;
import io.spaceurgent.iot.application.task.CheckPowerChangesTask;
import io.spaceurgent.iot.domain.model.PowerEvent;
import io.spaceurgent.iot.domain.repository.PowerEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final ExecutorService executorService;
    private final PowerEventRepository powerEventRepository;
    private final CheckPowerChangesTask checkPowerChangesTask;

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
        powerEvent = powerEventRepository.save(powerEvent);
        PowerEvent finalPowerEvent = powerEvent;
        executorService.execute(() -> checkPowerChangesTask.run(finalPowerEvent));
    }
}
