package com.logistics.domain.delivery.presentation;

import com.logistics.domain.delivery.application.DeliveryService;
import com.logistics.domain.delivery.infrastructure.DeliverySseEmitterRepository;
import com.logistics.domain.delivery.presentation.dto.DeliveryLocationUpdateRequest;
import com.logistics.domain.delivery.presentation.dto.DeliveryResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "배송/관제", description = "실시간 배송 위치 조회(SSE) 및 갱신 API")
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private static final long SSE_TIMEOUT = 10 * 60 * 1000L; // 10분

    private final DeliveryService deliveryService;
    private final DeliverySseEmitterRepository sseEmitterRepository;

    @PostMapping
    public ApiResponse<DeliveryResponse> create(@RequestParam Long dispatchId) {
        return ApiResponse.success(DeliveryResponse.from(deliveryService.createDelivery(dispatchId)));
    }

    @GetMapping("/{id}")
    public ApiResponse<DeliveryResponse> getOne(@PathVariable Long id) {
        return ApiResponse.success(DeliveryResponse.from(deliveryService.getDelivery(id)));
    }

    @PatchMapping("/{id}/location")
    public ApiResponse<DeliveryResponse> updateLocation(@PathVariable Long id,
                                                          @RequestBody DeliveryLocationUpdateRequest request) {
        return ApiResponse.success(DeliveryResponse.from(
                deliveryService.updateLocation(id, request.latitude(), request.longitude())));
    }

    /**
     * 실시간 배송 위치 구독 (Server-Sent Events)
     */
    @GetMapping(value = "/{id}/subscribe", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long id) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        return sseEmitterRepository.save(id, emitter);
    }
}
