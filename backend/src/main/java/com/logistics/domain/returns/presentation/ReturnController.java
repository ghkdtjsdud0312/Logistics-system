package com.logistics.domain.returns.presentation;

import com.logistics.domain.returns.application.ReturnQueryService;
import com.logistics.domain.returns.application.ReturnService;
import com.logistics.domain.returns.domain.ReturnStatus;
import com.logistics.domain.returns.presentation.dto.ReceiveRequest;
import com.logistics.domain.returns.presentation.dto.ReturnResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "반품", description = "반품 목록, 회수, 반품입고, 처리완료 API")
@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;
    private final ReturnQueryService queryService;

    @GetMapping
    public ApiResponse<List<ReturnResponse>> getList(@RequestParam(required = false) ReturnStatus status) {
        return ApiResponse.success(queryService.getList(status));
    }

    @GetMapping("/{id}")
    public ApiResponse<ReturnResponse> getOne(@PathVariable Long id) {
        return ApiResponse.success(queryService.getDetail(id));
    }

    @PatchMapping("/{id}/collect")
    public ApiResponse<ReturnResponse> collect(@PathVariable Long id) {
        return ApiResponse.success(queryService.toResponse(returnService.collect(id)));
    }

    @PatchMapping("/{id}/collected")
    public ApiResponse<ReturnResponse> collected(@PathVariable Long id) {
        return ApiResponse.success(queryService.toResponse(returnService.collected(id)));
    }

    @PatchMapping("/{id}/receive")
    public ApiResponse<ReturnResponse> receive(@PathVariable Long id, @RequestBody(required = false) ReceiveRequest request) {
        Long locationId = request == null ? null : request.locationId();
        return ApiResponse.success(queryService.toResponse(returnService.receive(id, locationId)));
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<ReturnResponse> complete(@PathVariable Long id) {
        return ApiResponse.success(queryService.toResponse(returnService.complete(id)));
    }
}
