package com.logistics.domain.inbound.presentation;

import com.logistics.domain.inbound.application.InboundQueryService;
import com.logistics.domain.inbound.application.InboundService;
import com.logistics.domain.inbound.domain.InboundStatus;
import com.logistics.domain.inbound.presentation.dto.InboundCreateRequest;
import com.logistics.domain.inbound.presentation.dto.InboundPutawayRequest;
import com.logistics.domain.inbound.presentation.dto.InboundResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "입고·적치", description = "입고 예정 등록, 입고완료, 적치 API")
@RestController
@RequestMapping("/api/inbounds")
@RequiredArgsConstructor
public class InboundController {

    private final InboundService inboundService;
    private final InboundQueryService queryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InboundResponse> create(@Valid @RequestBody InboundCreateRequest request) {
        return ApiResponse.success(queryService.toResponse(inboundService.create(
                request.partnerName(), request.productId(), request.quantity(), request.inboundDate())));
    }

    @GetMapping
    public ApiResponse<List<InboundResponse>> getList(@RequestParam(required = false) InboundStatus status) {
        return ApiResponse.success(queryService.getList(status));
    }

    @GetMapping("/{id}")
    public ApiResponse<InboundResponse> getOne(@PathVariable Long id) {
        return ApiResponse.success(queryService.toResponse(inboundService.get(id)));
    }

    @PatchMapping("/{id}/receive")
    public ApiResponse<InboundResponse> receive(@PathVariable Long id) {
        return ApiResponse.success(queryService.toResponse(inboundService.receive(id)));
    }

    @PatchMapping("/{id}/putaway")
    public ApiResponse<InboundResponse> putaway(@PathVariable Long id,
                                                @Valid @RequestBody InboundPutawayRequest request) {
        return ApiResponse.success(queryService.toResponse(inboundService.putaway(id, request.locationId())));
    }
}
