package io.spaceurgent.iot.infrastructure.telegram;

import io.spaceurgent.iot.application.interfaces.NotificationTextFactory;
import io.spaceurgent.iot.shared.TimeUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TelegramNotificationTextFactory implements NotificationTextFactory {

    public static final String POWER_OFF_TEMPLATE = """
    🔴 <b>СВІТЛО ВИМКНУЛИ</b>
   
    🕐 Час відключення: %s
    """;

    @Override
    public String powerOnNotification(Instant powerOnTime, Instant powerOffTime) {

        StringBuilder textBuilder = new StringBuilder()
                .append("🟢 <b>СВІТЛО З'ЯВИЛОСЯ</b>")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("🕐 Час відновлення: ")
                .append(formatTime(powerOnTime));
        if (powerOffTime != null) {
            textBuilder
                    .append(System.lineSeparator())
                    .append("⏳ Тривалість відключення: ")
                    .append(formatDuration(powerOnTime, powerOffTime));
        }
        return textBuilder.toString();
    }

    @Override
    public String powerOffNotification(Instant powerOffTime) {
        return POWER_OFF_TEMPLATE.formatted(formatTime(powerOffTime));
    }

    private String formatTime(Instant powerOnTime) {
        LocalDateTime kyivTime = TimeUtils.toKyivTime(powerOnTime);
        return kyivTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
    }

    private String formatDuration(Instant powerOnTime, Instant powerOffTime) {
        Duration duration = Duration.between(powerOnTime, powerOffTime);
        if (duration.isNegative()) {
            duration = duration.abs();
        }
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        StringBuilder durationBuilder = new StringBuilder();
        if (hours > 0) {
            durationBuilder
                    .append(hours)
                    .append(" год. ");
        }
        durationBuilder
                .append(minutes)
                .append(" хв. ")
                .append(seconds)
                .append(" сек. ");
        return durationBuilder.toString();
    }
}
