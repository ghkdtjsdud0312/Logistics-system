package com.logistics.domain.returns.application;

import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.returns.domain.ReturnOrder;
import com.logistics.domain.returns.domain.ReturnOrderRepository;
import com.logistics.domain.returns.domain.ReturnStatus;
import com.logistics.domain.returns.presentation.dto.ReturnResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 반품 목록/상세 조회. 주문번호·고객·상품은 order/master 서비스로 조회한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReturnQueryService {

    private final ReturnOrderRepository returnRepository;
    private final ReturnService returnService;
    private final OrderService orderService;
    private final ProductService productService;

    public List<ReturnResponse> getList(ReturnStatus status) {
        return toResponses(returnRepository.findAllByStatus(status));
    }

    public ReturnResponse getDetail(Long id) {
        return toResponses(List.of(returnService.get(id))).get(0);
    }

    public ReturnResponse toResponse(ReturnOrder returnOrder) {
        return toResponses(List.of(returnOrder)).get(0);
    }

    private List<ReturnResponse> toResponses(List<ReturnOrder> returns) {
        Map<Long, Order> orders = orderService.getOrderMap(
                returns.stream().map(ReturnOrder::getOrderId).collect(Collectors.toSet()));
        Map<Long, Product> products = productService.getProductMap(orders.values().stream()
                .flatMap(o -> o.getItems().stream()).map(i -> i.getProductId()).collect(Collectors.toSet()));
        return returns.stream().map(r -> ReturnResponse.of(r, orders.get(r.getOrderId()), products)).toList();
    }
}
