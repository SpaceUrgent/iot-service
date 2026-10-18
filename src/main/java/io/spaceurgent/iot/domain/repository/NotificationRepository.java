package io.spaceurgent.iot.domain.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.spaceurgent.iot.domain.model.Notification;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificationRepository implements PanacheRepository<Notification> {
}
