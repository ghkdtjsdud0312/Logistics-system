package com.logistics.domain.order.presentation;

import com.logistics.domain.order.application.OrderQueryService;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.domain.order.presentation.dto.OrderCreateRequest;
import com.logistics.domain.order.presentation.dto.OrderCreateResponse;
import com.logistics.domain.order.presentation.dto.OrderDetailResponse;
import com.logistics.domain.order.presentation.dto.OrderListResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "주문 관리", description = "주문 생성, 목록, 상세 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderQueryService queryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderCreateResponse> create(@Valid @RequestBody OrderCreateRequest request) {
        return ApiResponse.success(OrderCreateResponse.from(orderService.create(request.toCommand())));
    }

    @GetMapping
    public ApiResponse<List<OrderListResponse>> search(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(queryService.search(
                new OrderSearchCriteria(orderNo, customerName, status, from, to, page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailResponse> getOne(@PathVariable Long id) {
        return ApiResponse.success(queryService.getDetail(id));
    }
}
