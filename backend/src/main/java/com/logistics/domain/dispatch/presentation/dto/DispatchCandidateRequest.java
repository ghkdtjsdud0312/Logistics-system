package com.logistics.domain.dispatch.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record DispatchCandidateRequest(
        @NotEmpty(message = "출고 계획은 1개 이상이어야 합니다.") List<Long> outboundIds,
        @NotNull(message = "배차 계획 시각은 필수입니다.") LocalDateTime plannedAt
) {
}
