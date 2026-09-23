package com.logistics.domain.anomaly.presentation.dto;

import com.logistics.domain.anomaly.domain.AnomalyStatus;
import jakarta.validation.constraints.NotNull;

public record AnomalyStatusChangeRequest(
        @NotNull(message = "변경할 상태는 필수입니다.") AnomalyStatus status
) {
}
