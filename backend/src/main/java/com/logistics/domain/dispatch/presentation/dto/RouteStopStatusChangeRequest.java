package com.logistics.domain.dispatch.presentation.dto;

import com.logistics.domain.dispatch.domain.RouteStopStatus;
import jakarta.validation.constraints.NotNull;

public record RouteStopStatusChangeRequest(
        @NotNull(message = "변경할 상태는 필수입니다.") RouteStopStatus status
) {
}
