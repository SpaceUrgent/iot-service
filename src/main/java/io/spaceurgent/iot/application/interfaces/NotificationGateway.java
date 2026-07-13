package io.spaceurgent.iot.application.interfaces;

import io.spaceurgent.iot.domain.model.Notification;

public interface NotificationGateway {

    Notification send(Notification notification);
}
