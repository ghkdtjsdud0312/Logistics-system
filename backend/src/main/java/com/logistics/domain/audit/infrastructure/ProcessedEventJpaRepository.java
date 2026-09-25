package com.logistics.domain.audit.infrastructure;

import com.logistics.domain.audit.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, ProcessedEvent.Key> {
}
