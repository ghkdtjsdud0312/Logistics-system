package com.logistics.domain.delivery.presentation.dto;

import com.logistics.domain.loading.domain.FailReason;
import jakarta.validation.constraints.NotNull;

public record FailRequest(@NotNull(message = "실패 사유는 필수입니다.") FailReason reason, String detail) {
}
