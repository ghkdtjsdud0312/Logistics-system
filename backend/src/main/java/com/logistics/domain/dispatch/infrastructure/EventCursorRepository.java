package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.EventCursor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventCursorRepository extends JpaRepository<EventCursor, Long> {

    Optional<EventCursor> findByConsumerNameAndAggregateId(String consumerName, String aggregateId);
}
