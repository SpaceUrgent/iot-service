package io.spaceurgent.iot.application.dto;

import java.time.Instant;

public record NewEventDto(
    EventType eventType,
    Instant timestamp,
    Integer batteryPercentage,
    Boolean powerPresent
) {
}
