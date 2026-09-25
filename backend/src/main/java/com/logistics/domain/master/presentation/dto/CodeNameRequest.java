package com.logistics.domain.master.presentation.dto;

import jakarta.validation.constraints.NotBlank;

/** 창고·구역 등록 요청 (코드, 이름) */
public record CodeNameRequest(
        @NotBlank(message = "코드는 필수입니다.") String code,
        @NotBlank(message = "이름은 필수입니다.") String name
) {
}
