package com.logistics.domain.master.presentation.dto;

import jakarta.validation.constraints.NotBlank;

/** 창고 수정 요청. 주소가 바뀌면 좌표를 다시 변환한다. */
public record WarehouseUpdateRequest(
        @NotBlank(message = "이름은 필수입니다.") String name,
        String address
) {
}
