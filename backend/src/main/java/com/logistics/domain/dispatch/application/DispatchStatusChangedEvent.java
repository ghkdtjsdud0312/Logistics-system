package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.DispatchStatus;

/**
 * 배차 상태가 성공적으로 바뀌었을 때 트랜잭션 내부에서 발행하는 스프링 이벤트.
 * 실제 Kafka 발행/SSE 브로드캐스트는 커밋 이후(AFTER_COMMIT)에만 수행한다 (DispatchEventRelay 참고).
 */
public record DispatchStatusChangedEvent(Long dispatchId, DispatchStatus fromStatus, DispatchStatus toStatus, long version) {
}
