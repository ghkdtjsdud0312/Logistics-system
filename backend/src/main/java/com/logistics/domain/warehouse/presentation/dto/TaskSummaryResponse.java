package com.logistics.domain.warehouse.presentation.dto;

import com.logistics.domain.warehouse.domain.WorkStatus;

/** 시작·완료 처리 결과 요약 */
public record TaskSummaryResponse(Long id, String taskNo, WorkStatus status) {
}
