package io.spaceurgent.iot.application.interfaces;

import java.time.Instant;

public interface NotificationTextFactory {

    String powerOnNotification(Instant powerOnTime, Instant powerOffTime);
    String powerOffNotification(Instant powerOffTime);
}
