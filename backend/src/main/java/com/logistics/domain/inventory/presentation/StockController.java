package com.logistics.domain.inventory.presentation;

import com.logistics.domain.inventory.application.StockQueryService;
import com.logistics.domain.inventory.application.StockStatus;
import com.logistics.domain.inventory.presentation.dto.StockResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "재고 현황", description = "현재/예약/가용 재고 조회 API")
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockQueryService stockQueryService;

    @GetMapping
    public ApiResponse<List<StockResponse>> search(@RequestParam(required = false) Long warehouseId,
                                                   @RequestParam(required = false) Long zoneId,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) StockStatus stockStatus) {
        return ApiResponse.success(stockQueryService.search(warehouseId, zoneId, keyword, stockStatus));
    }
}
