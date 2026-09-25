package com.logistics.domain.audit.infrastructure;

import com.logistics.domain.audit.domain.ProcessedEvent;
import com.logistics.domain.audit.domain.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class ProcessedEventRepositoryImpl implements ProcessedEventRepository {

    private final ProcessedEventJpaRepository jpaRepository;
    private final Clock clock;

    @Override
    public boolean markProcessed(String consumerName, String eventId) {
        if (jpaRepository.existsById(new ProcessedEvent.Key(consumerName, eventId))) {
            return false;
        }
        jpaRepository.save(new ProcessedEvent(consumerName, eventId, LocalDateTime.now(clock)));
        return true;
    }
}
