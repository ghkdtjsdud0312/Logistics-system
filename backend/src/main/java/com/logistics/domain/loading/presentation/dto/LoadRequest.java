package com.logistics.domain.loading.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record LoadRequest(@NotEmpty(message = "상차할 주문을 선택하세요.") List<Long> orderIds) {
}
