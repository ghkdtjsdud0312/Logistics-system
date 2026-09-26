package com.logistics.domain.master.presentation.dto;

import jakarta.validation.constraints.NotBlank;

/** 창고·구역 이름 수정 요청 */
public record NameRequest(@NotBlank(message = "이름은 필수입니다.") String name) {
}
