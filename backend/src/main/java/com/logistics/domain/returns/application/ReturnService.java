package com.logistics.domain.returns.application;

import com.logistics.domain.delivery.application.ShipmentFailedEvent;
import com.logistics.domain.inventory.application.StockService;
import com.logistics.domain.master.application.LocationQueryService;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderItem;
import com.logistics.domain.returns.domain.ReturnOrder;
import com.logistics.domain.returns.domain.ReturnOrderRepository;
import com.logistics.domain.returns.domain.ReturnStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.event.StatusChangedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 반품 생성(배송 실패 이벤트 수신)과 회수·반품입고·처리완료. 반품입고 시 재고를 복구한다. */
@Service
@RequiredArgsConstructor
@Transactional
public class ReturnService {

    private final ReturnOrderRepository returnRepository;
    private final OrderService orderService;
    private final StockService stockService;
    private final LocationQueryService locationQueryService;
    private final StatusChangedEventPublisher eventPublisher;

    /** 배송 실패 처리와 같은 트랜잭션에서 실행되어 반품이 정확히 한 번 만들어진다. */
    @EventListener
    public void onShipmentFailed(ShipmentFailedEvent event) {
        ReturnOrder created = returnRepository.save(
                new ReturnOrder(event.orderId(), event.shipmentId(), event.reason(), event.quantity()));
        created.assignNo();
        publish(created, "CREATE", null, ReturnStatus.REQUESTED);
    }

    public ReturnOrder collect(Long id) {
        ReturnOrder returnOrder = get(id);
        returnOrder.collect();
        publish(returnOrder, "COLLECT_START", ReturnStatus.REQUESTED, ReturnStatus.COLLECTING);
        return returnOrder;
    }

    public ReturnOrder collected(Long id) {
        ReturnOrder returnOrder = get(id);
        returnOrder.collected();
        publish(returnOrder, "COLLECTED", ReturnStatus.COLLECTING, ReturnStatus.COLLECTED);
        return returnOrder;
    }

    /** 파손이 아니면 지정 위치에 주문 품목별 재고를 복구한다. */
    public ReturnOrder receive(Long id, Long locationId) {
        ReturnOrder returnOrder = get(id);
        returnOrder.receive(locationId);
        if (returnOrder.restocks()) {
            locationQueryService.getLocationInfo(locationId);
            Order order = orderService.get(returnOrder.getOrderId());
            for (OrderItem item : order.getItems()) {
                stockService.increase(item.getProductId(), locationId, item.getQuantity());
            }
        }
        publish(returnOrder, "RETURN_RECEIVE", ReturnStatus.COLLECTED, ReturnStatus.RETURN_RECEIVED);
        return returnOrder;
    }

    public ReturnOrder complete(Long id) {
        ReturnOrder returnOrder = get(id);
        returnOrder.complete();
        publish(returnOrder, "COMPLETE", ReturnStatus.RETURN_RECEIVED, ReturnStatus.COMPLETED);
        return returnOrder;
    }

    @Transactional(readOnly = true)
    public ReturnOrder get(Long id) {
        return returnRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RETURN_NOT_FOUND));
    }

    private void publish(ReturnOrder r, String action, ReturnStatus from, ReturnStatus to) {
        eventPublisher.publish("RETURN", r.getId(), r.getReturnNo(), r.getOrderId(), action,
                from == null ? null : from.name(), to.name());
    }
}
