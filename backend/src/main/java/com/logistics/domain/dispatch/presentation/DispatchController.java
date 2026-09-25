package com.logistics.domain.dispatch.presentation;

import com.logistics.domain.dispatch.application.DispatchQueryService;
import com.logistics.domain.dispatch.application.DispatchRegistrationService;
import com.logistics.domain.dispatch.application.DispatchService;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.presentation.dto.DispatchRegisterRequest;
import com.logistics.domain.dispatch.presentation.dto.DispatchResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "배차", description = "배차 등록, 조회, 배송 시작, 취소 API")
@RestController
@RequestMapping("/api/dispatches")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchRegistrationService registrationService;
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

    @PatchMapping("/{id}/start")
    public ApiResponse<DispatchResponse> start(@PathVariable Long id) {
        return ApiResponse.success(queryService.toResponse(dispatchService.start(id)));
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<DispatchResponse> cancel(@PathVariable Long id) {
        return ApiResponse.success(queryService.toResponse(dispatchService.cancel(id)));
    }
}
