package com.logistics.domain.dispatch.presentation;

import com.logistics.domain.dispatch.application.*;
import com.logistics.domain.dispatch.presentation.dto.*;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "배차 관리", description = "차량 후보 조회, 배차 확정, 경로 최적화, 상태 관리 API")
@RestController
@RequestMapping("/api/dispatches")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchCandidateService dispatchCandidateService;
    private final DispatchConfirmService dispatchConfirmService;
    private final DispatchService dispatchService;
    private final RouteOptimizationService routeOptimizationService;
    private final DispatchStatusService dispatchStatusService;
    private final RouteStopService routeStopService;
    private final DispatchDetailService dispatchDetailService;

    @PostMapping("/candidates")
    public ApiResponse<DispatchCandidateResponse> findCandidates(@Valid @RequestBody DispatchCandidateRequest request) {
        return ApiResponse.success(dispatchCandidateService.findCandidates(request));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DispatchResponse> confirm(@Valid @RequestBody DispatchConfirmRequest request) {
        return ApiResponse.success(DispatchResponse.from(dispatchConfirmService.confirm(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<DispatchDetailResponse> getOne(@PathVariable Long id) {
        return ApiResponse.success(dispatchDetailService.getDetail(id));
    }

    @GetMapping
    public ApiResponse<List<DispatchResponse>> getList() {
        List<DispatchResponse> responses = dispatchService.getDispatchList().stream()
                .map(DispatchResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @PostMapping("/{id}/route/optimize")
    public ApiResponse<RouteOptimizeResponse> optimizeRoute(@PathVariable Long id) {
        return ApiResponse.success(RouteOptimizeResponse.from(routeOptimizationService.optimize(id)));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<DispatchResponse> changeStatus(@PathVariable Long id, @Valid @RequestBody DispatchStatusChangeRequest request) {
        var dispatch = dispatchStatusService.changeStatus(
                id, request.status(), request.expectedVersion(), request.actor(), request.description());
        return ApiResponse.success(DispatchResponse.from(dispatch));
    }

    @PatchMapping("/{id}/stops/{stopId}")
    public ApiResponse<RouteStopDto> changeStopStatus(@PathVariable Long id, @PathVariable Long stopId,
                                                        @Valid @RequestBody RouteStopStatusChangeRequest request) {
        return ApiResponse.success(RouteStopDto.from(routeStopService.changeStopStatus(id, stopId, request.status())));
    }
}
