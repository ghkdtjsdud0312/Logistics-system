package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, Long> {

    boolean existsByConsumerNameAndEventId(String consumerName, String eventId);
}
