package com.logistics.domain.dispatch.presentation;

import com.logistics.domain.dispatch.application.DispatchService;
import com.logistics.domain.dispatch.presentation.dto.DispatchCreateRequest;
import com.logistics.domain.dispatch.presentation.dto.DispatchResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "배차 최적화", description = "Nearest Neighbor 기반 배차 경로 최적화 API")
@RestController
@RequestMapping("/api/dispatches")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DispatchResponse> create(@Valid @RequestBody DispatchCreateRequest request) {
        return ApiResponse.success(DispatchResponse.from(dispatchService.createDispatch(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<DispatchResponse> getOne(@PathVariable Long id) {
        return ApiResponse.success(DispatchResponse.from(dispatchService.getDispatch(id)));
    }

    @GetMapping
    public ApiResponse<List<DispatchResponse>> getList() {
        List<DispatchResponse> responses = dispatchService.getDispatchList().stream()
                .map(DispatchResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @PatchMapping("/{id}/start")
    public ApiResponse<DispatchResponse> start(@PathVariable Long id) {
        return ApiResponse.success(DispatchResponse.from(dispatchService.startDispatch(id)));
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<DispatchResponse> complete(@PathVariable Long id) {
        return ApiResponse.success(DispatchResponse.from(dispatchService.completeDispatch(id)));
    }
}
