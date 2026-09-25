package com.logistics.domain.delivery.application;

import com.logistics.domain.delivery.presentation.dto.DeliveryStatusResponse;
import com.logistics.domain.dispatch.application.DispatchQueryService;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.presentation.dto.DispatchResponse;
import com.logistics.domain.loading.application.ShipmentQueryService;
import com.logistics.domain.loading.presentation.dto.ShipmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

/** 배송현황: 진행 중인 배차(대기·배송중)별 진행률과 주문별 배송 상태 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryStatusQueryService {

    private final DispatchQueryService dispatchQueryService;
    private final ShipmentQueryService shipmentQueryService;

    public List<DeliveryStatusResponse> getBoard() {
        return Stream.of(DispatchStatus.IN_TRANSIT, DispatchStatus.REGISTERED)
                .flatMap(status -> dispatchQueryService.getList(status).stream())
                .map(this::toResponse).toList();
    }

    private DeliveryStatusResponse toResponse(DispatchResponse dispatch) {
        List<ShipmentResponse> shipments = shipmentQueryService.search(null, dispatch.id());
        return DeliveryStatusResponse.of(dispatch, shipments);
    }
}
