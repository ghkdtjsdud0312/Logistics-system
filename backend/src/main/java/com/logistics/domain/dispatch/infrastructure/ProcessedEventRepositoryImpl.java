package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.ProcessedEvent;
import com.logistics.domain.dispatch.domain.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProcessedEventRepositoryImpl implements ProcessedEventRepository {

    private final ProcessedEventJpaRepository jpaRepository;

    @Override
    public boolean existsByConsumerNameAndEventId(String consumerName, String eventId) {
        return jpaRepository.existsByConsumerNameAndEventId(consumerName, eventId);
    }

    @Override
    public ProcessedEvent save(ProcessedEvent processedEvent) {
        return jpaRepository.save(processedEvent);
    }
}
