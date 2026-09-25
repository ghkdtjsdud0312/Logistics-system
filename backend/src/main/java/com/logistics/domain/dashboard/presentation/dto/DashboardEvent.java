package com.logistics.domain.dashboard.presentation.dto;

import java.time.LocalDateTime;

/** 최근 물류 이벤트와 SSE로 보내는 이벤트 한 줄 */
public record DashboardEvent(LocalDateTime at, String description) {
}
