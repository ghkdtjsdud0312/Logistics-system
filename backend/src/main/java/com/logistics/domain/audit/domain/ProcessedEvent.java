package com.logistics.domain.audit.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Consumer가 이미 처리한 이벤트. (consumerName, eventId) 복합 키로 중복 처리를 막는다. */
@Getter
@Entity
@Table(name = "processed_event")
@IdClass(ProcessedEvent.Key.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessedEvent {

    @Id
    private String consumerName;

    @Id
    private String eventId;

    private LocalDateTime processedAt;

    public ProcessedEvent(String consumerName, String eventId, LocalDateTime processedAt) {
        this.consumerName = consumerName;
        this.eventId = eventId;
        this.processedAt = processedAt;
    }

    public record Key(String consumerName, String eventId) implements Serializable {
    }
}
