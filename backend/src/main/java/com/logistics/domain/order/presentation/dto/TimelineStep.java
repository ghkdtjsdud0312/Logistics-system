package com.logistics.domain.order.presentation.dto;

import com.logistics.domain.order.domain.OrderStatus;

import java.time.LocalDateTime;

/** 물류 진행 타임라인의 한 단계 */
public record TimelineStep(OrderStatus step, boolean done, LocalDateTime at) {
}
