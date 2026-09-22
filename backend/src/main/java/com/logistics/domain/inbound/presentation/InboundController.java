package com.logistics.domain.inbound.presentation;

import com.logistics.domain.inbound.application.InboundService;
import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.presentation.dto.InboundCompleteRequest;
import com.logistics.domain.inbound.presentation.dto.InboundCreateRequest;
import com.logistics.domain.inbound.presentation.dto.InboundResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "입고 관리", description = "입고 등록/조회 API")
@RestController
@RequestMapping("/api/inbounds")
@RequiredArgsConstructor
public class InboundController {

    private final InboundService inboundService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InboundResponse> create(@Valid @RequestBody InboundCreateRequest request) {
        return ApiResponse.success(InboundResponse.from(inboundService.createInbound(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<InboundResponse> getOne(@PathVariable Long id) {
        return ApiResponse.success(InboundResponse.from(inboundService.getInbound(id)));
    }

    @GetMapping
    public ApiResponse<List<InboundResponse>> getList() {
        List<InboundResponse> responses = inboundService.getInboundList().stream()
                .map(InboundResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @PatchMapping("/{id}/start")
    public ApiResponse<InboundResponse> start(@PathVariable Long id) {
        return ApiResponse.success(InboundResponse.from(inboundService.startInbound(id)));
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<InboundResponse> complete(
            @PathVariable Long id,
            @Valid @RequestBody InboundCompleteRequest request) {
        Inbound inbound = inboundService.completeInbound(id, request.inspectedQuantity());
        return ApiResponse.success(InboundResponse.from(inbound));
    }
}
