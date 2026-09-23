package com.logistics.domain.dispatch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Consumer별로 aggregate의 마지막 처리 버전을 기억해 역순 도착 이벤트를 걸러낸다. */
@Getter
@Entity
@Table(name = "event_cursor", uniqueConstraints = @UniqueConstraint(columnNames = {"consumer_name", "aggregate_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventCursor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consumer_name", nullable = false)
    private String consumerName;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "last_version", nullable = false)
    private long lastVersion;

    public static EventCursor of(String consumerName, String aggregateId, long version) {
        EventCursor cursor = new EventCursor();
        cursor.consumerName = consumerName;
        cursor.aggregateId = aggregateId;
        cursor.lastVersion = version;
        return cursor;
    }

    public void advanceTo(long version) {
        this.lastVersion = version;
    }
}
