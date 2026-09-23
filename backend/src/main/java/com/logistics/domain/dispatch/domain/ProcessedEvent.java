package com.logistics.domain.dispatch.domain;

import com.logistics.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Kafka Consumer 멱등 처리 기록: (consumerName, eventId) 조합은 한 번만 존재한다. */
@Getter
@Entity
@Table(name = "processed_event", uniqueConstraints = @UniqueConstraint(columnNames = {"consumer_name", "event_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessedEvent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consumer_name", nullable = false)
    private String consumerName;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Builder
    public ProcessedEvent(String consumerName, String eventId) {
        this.consumerName = consumerName;
        this.eventId = eventId;
    }
}
