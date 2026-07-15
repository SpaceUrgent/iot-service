package io.spaceurgent.iot.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "notifications")

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "tracking_id")
    private String trackingId;

    @Column(name = "error_descriptions", columnDefinition = "text")
    private String errorDescription;

    private String tag;

    @Transient
    private String text;

    public enum Status {
        SENT,
        ERROR
    }

    public final static class Tags {
        public static final String POWER_ON = "power-on";
        public static final String POWER_OFF = "power-off";
    }
}
