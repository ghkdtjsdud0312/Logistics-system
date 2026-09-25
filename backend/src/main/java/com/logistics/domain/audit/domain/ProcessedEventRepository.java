package com.logistics.domain.audit.domain;

public interface ProcessedEventRepository {

    /** 처음 처리하는 이벤트면 처리 기록을 남기고 true, 이미 처리했다면 false */
    boolean markProcessed(String consumerName, String eventId);
}
