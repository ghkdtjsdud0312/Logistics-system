package com.logistics.domain.outbound.presentation;

import com.logistics.domain.outbound.application.OutboundService;
import com.logistics.domain.outbound.presentation.dto.AvailableInboundResponse;
import com.logistics.domain.outbound.presentation.dto.OutboundCreateRequest;
import com.logistics.domain.outbound.presentation.dto.OutboundResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "출고 관리", description = "출고 계획 등록/조회 API")
@RestController
@RequestMapping("/api/outbounds")
@RequiredArgsConstructor
public class OutboundController {

    private final OutboundService outboundService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OutboundResponse> create(@Valid @RequestBody OutboundCreateRequest request) {
        return ApiResponse.success(OutboundResponse.from(outboundService.createOutbound(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<OutboundResponse> getOne(@PathVariable Long id) {
        return ApiResponse.success(OutboundResponse.from(outboundService.getOutbound(id)));
    }

    @GetMapping
    public ApiResponse<List<OutboundResponse>> getList() {
        List<OutboundResponse> responses = outboundService.getOutboundList().stream()
                .map(OutboundResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/available-inbounds")
    public ApiResponse<List<AvailableInboundResponse>> getAvailableInbounds() {
        return ApiResponse.success(outboundService.getAvailableInbounds());
    }

    @PatchMapping("/{id}/pick")
    public ApiResponse<OutboundResponse> pick(@PathVariable Long id) {
        return ApiResponse.success(OutboundResponse.from(outboundService.pickOutbound(id)));
    }

    @PatchMapping("/{id}/ship")
    public ApiResponse<OutboundResponse> ship(@PathVariable Long id) {
        return ApiResponse.success(OutboundResponse.from(outboundService.shipOutbound(id)));
    }
}
