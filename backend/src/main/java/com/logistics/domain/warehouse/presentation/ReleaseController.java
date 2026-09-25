package com.logistics.domain.warehouse.presentation;

import com.logistics.domain.warehouse.application.ReleaseService;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "출고 지시", description = "주문 출고 지시(피킹 작업 생성) API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ReleaseController {

    private final ReleaseService releaseService;

    @PostMapping("/{id}/release")
    public ApiResponse<ReleaseService.Released> release(@PathVariable Long id) {
        return ApiResponse.success(releaseService.release(id));
    }
}
