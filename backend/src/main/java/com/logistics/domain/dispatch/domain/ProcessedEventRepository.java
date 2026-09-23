package com.logistics.domain.dispatch.domain;

public interface ProcessedEventRepository {

    boolean existsByConsumerNameAndEventId(String consumerName, String eventId);

    ProcessedEvent save(ProcessedEvent processedEvent);
}
