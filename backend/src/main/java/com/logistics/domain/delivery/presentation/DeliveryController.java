package com.logistics.domain.delivery.presentation;

import com.logistics.domain.delivery.application.DeliveryService;
import com.logistics.domain.delivery.application.DeliveryStatusQueryService;
import com.logistics.domain.delivery.presentation.dto.DeliverRequest;
import com.logistics.domain.delivery.presentation.dto.DeliveryStatusResponse;
import com.logistics.domain.delivery.presentation.dto.FailRequest;
import com.logistics.domain.loading.application.ShipmentQueryService;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.presentation.dto.ShipmentResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "배송", description = "배송현황과 배송 완료·실패 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryStatusQueryService statusQueryService;
    private final ShipmentQueryService shipmentQueryService;

    @GetMapping("/delivery-status")
    public ApiResponse<List<DeliveryStatusResponse>> getStatus() {
        return ApiResponse.success(statusQueryService.getBoard());
    }

    @PatchMapping("/shipments/{id}/deliver")
    public ApiResponse<ShipmentResponse> deliver(@PathVariable Long id, @Valid @RequestBody DeliverRequest request) {
        return ApiResponse.success(one(deliveryService.deliver(id, request.deliveredQty())));
    }

    @PatchMapping("/shipments/{id}/fail")
    public ApiResponse<ShipmentResponse> fail(@PathVariable Long id, @Valid @RequestBody FailRequest request) {
        return ApiResponse.success(one(deliveryService.fail(id, request.reason(), request.detail())));
    }

    private ShipmentResponse one(Shipment shipment) {
        return shipmentQueryService.toResponses(List.of(shipment)).get(0);
    }
}
