package com.logistics.domain.dispatch.presentation;

import com.logistics.domain.dispatch.application.DispatchLoadingService;
import com.logistics.domain.dispatch.application.DispatchQueryService;
import com.logistics.domain.dispatch.application.DispatchRouteService;
import com.logistics.domain.dispatch.application.DispatchRegistrationService;
import com.logistics.domain.dispatch.application.DispatchService;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.presentation.dto.DispatchRegisterRequest;
import com.logistics.domain.dispatch.presentation.dto.DispatchResponse;
import com.logistics.domain.dispatch.presentation.dto.DispatchShipmentsRequest;
import com.logistics.domain.dispatch.presentation.dto.DispatchWarehouseRequest;
import com.logistics.domain.dispatch.presentation.dto.RouteOptimizeResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "배차", description = "배차 생성, 화물 적재, 마감, 배송 시작, 취소 API")
@RestController
@RequestMapping("/api/dispatches")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchRegistrationService registrationService;
    private final DispatchLoadingService loadingService;
    private final DispatchRouteService routeService;
    private final DispatchService dispatchService;
    private final DispatchQueryService queryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DispatchResponse> register(@Valid @RequestBody DispatchRegisterRequest request) {
        return ApiResponse.success(queryService.toResponse(registrationService.register(request.toCommand())));
    }

    @GetMapping
    public ApiResponse<List<DispatchResponse>> getList(@RequestParam(required = false) DispatchStatus status) {
        return ApiResponse.success(queryService.getList(status));
    }

    @GetMapping("/{id}")
    public ApiResponse<DispatchResponse> getOne(@PathVariable Long id) {
        return ApiResponse.success(queryService.getDetail(id));
    }

    @PostMapping("/{id}/shipments")
    public ApiResponse<DispatchResponse> addShipments(@PathVariable Long id,
                                                      @Valid @RequestBody DispatchShipmentsRequest request) {
        return ApiResponse.success(queryService.toResponse(loadingService.addShipments(id, request.shipmentIds())));
    }

    @DeleteMapping("/{id}/shipments/{shipmentId}")
    public ApiResponse<DispatchResponse> removeShipment(@PathVariable Long id, @PathVariable Long shipmentId) {
        return ApiResponse.success(queryService.toResponse(loadingService.removeShipment(id, shipmentId)));
    }

    @PatchMapping("/{id}/warehouse")
    public ApiResponse<DispatchResponse> changeWarehouse(@PathVariable Long id,
                                                         @Valid @RequestBody DispatchWarehouseRequest request) {
        return ApiResponse.success(queryService.toResponse(routeService.changeOrigin(id, request.warehouseId())));
    }

    @PutMapping("/{id}/route")
    public ApiResponse<DispatchResponse> reorder(@PathVariable Long id,
                                                 @Valid @RequestBody DispatchShipmentsRequest request) {
        return ApiResponse.success(queryService.toResponse(routeService.reorder(id, request.shipmentIds())));
    }

    @PostMapping("/{id}/route/optimize")
    public ApiResponse<RouteOptimizeResponse> optimize(@PathVariable Long id) {
        List<Long> unlocated = routeService.optimize(id);
        return ApiResponse.success(new RouteOptimizeResponse(queryService.getDetail(id), unlocated));
    }

    @PatchMapping("/{id}/close")
    public ApiResponse<DispatchResponse> close(@PathVariable Long id) {
        return ApiResponse.success(queryService.toResponse(loadingService.close(id)));
    }

    @PatchMapping("/{id}/start")
    public ApiResponse<DispatchResponse> start(@PathVariable Long id) {
        return ApiResponse.success(queryService.toResponse(dispatchService.start(id)));
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<DispatchResponse> cancel(@PathVariable Long id) {
        return ApiResponse.success(queryService.toResponse(dispatchService.cancel(id)));
    }
}
