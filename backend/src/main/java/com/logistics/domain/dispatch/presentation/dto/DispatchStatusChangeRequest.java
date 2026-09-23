package com.logistics.domain.dispatch.presentation.dto;

import com.logistics.domain.dispatch.domain.DispatchStatus;
import jakarta.validation.constraints.NotNull;

public record DispatchStatusChangeRequest(
        @NotNull(message = "변경할 상태는 필수입니다.") DispatchStatus status,
        @NotNull(message = "expectedVersion은 필수입니다.") Long expectedVersion,
        String actor,
        String description
) {
}
