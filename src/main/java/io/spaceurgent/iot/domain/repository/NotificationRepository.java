package io.spaceurgent.iot.domain.repository;

import io.spaceurgent.iot.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
