package com.logistics.domain.loading.presentation;

import com.logistics.domain.loading.application.ShipmentQueryService;
import com.logistics.domain.loading.domain.ShipmentStatus;
import com.logistics.domain.loading.presentation.dto.ShipmentResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "배송 단위", description = "Shipment 목록 조회 API (배차 대기는 status=LOADED)")
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentQueryService queryService;

    @GetMapping
    public ApiResponse<List<ShipmentResponse>> search(@RequestParam(required = false) ShipmentStatus status,
                                                      @RequestParam(required = false) Long dispatchId) {
        return ApiResponse.success(queryService.search(status, dispatchId));
    }
}
