package com.logistics.domain.master.presentation.dto;

import jakarta.validation.constraints.NotBlank;

/** 창고 등록 요청. 주소는 선택이며 있으면 좌표로 변환해 저장한다. */
public record WarehouseCreateRequest(
        @NotBlank(message = "코드는 필수입니다.") String code,
        @NotBlank(message = "이름은 필수입니다.") String name,
        String address
) {
}
