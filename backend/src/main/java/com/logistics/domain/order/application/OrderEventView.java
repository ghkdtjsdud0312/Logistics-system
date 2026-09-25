package com.logistics.domain.order.application;

import java.time.LocalDateTime;

/** 주문에 관련된 이벤트 한 건. toStatus는 주문 자체의 상태 변경일 때만 값이 있다. */
public record OrderEventView(LocalDateTime at, String description, String toStatus) {
}
