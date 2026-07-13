package io.spaceurgent.iot.shared;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class TimeUtils {

    private TimeUtils() {
    }

    public static final ZoneId KYIV_ZONE = ZoneId.of("Europe/Kyiv");

    public static LocalDateTime toKyivTime(Instant instant) {
        return instant.atZone(KYIV_ZONE).toLocalDateTime();
    }
}
