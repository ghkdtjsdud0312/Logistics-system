package com.logistics.domain.loading.presentation;

import com.logistics.domain.loading.application.LoadingService;
import com.logistics.domain.loading.application.ShipmentQueryService;
import com.logistics.domain.loading.presentation.dto.LoadRequest;
import com.logistics.domain.loading.presentation.dto.ShipmentResponse;
import com.logistics.domain.order.application.OrderQueryService;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.domain.order.presentation.dto.OrderListResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상차", description = "상차 대기 주문 조회와 상차 완료 API")
@RestController
@RequestMapping("/api/loadings")
@RequiredArgsConstructor
public class LoadingController {

    private final LoadingService loadingService;
    private final ShipmentQueryService shipmentQueryService;
    private final OrderQueryService orderQueryService;

    @GetMapping("/waiting-orders")
    public ApiResponse<List<OrderListResponse>> waitingOrders() {
        return ApiResponse.success(orderQueryService.search(
                new OrderSearchCriteria(null, null, OrderStatus.PACKED, null, null, 0, 200)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<List<ShipmentResponse>> load(@Valid @RequestBody LoadRequest request) {
        return ApiResponse.success(shipmentQueryService.toResponses(loadingService.load(request.orderIds())));
    }
}
