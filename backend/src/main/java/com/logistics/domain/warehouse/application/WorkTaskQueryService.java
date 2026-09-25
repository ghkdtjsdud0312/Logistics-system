package com.logistics.domain.warehouse.application;

import com.logistics.domain.master.application.LocationInfo;
import com.logistics.domain.master.application.LocationQueryService;
import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.warehouse.domain.PackingTask;
import com.logistics.domain.warehouse.domain.PackingTaskRepository;
import com.logistics.domain.warehouse.domain.PickingTask;
import com.logistics.domain.warehouse.domain.PickingTaskRepository;
import com.logistics.domain.warehouse.domain.WorkStatus;
import com.logistics.domain.warehouse.presentation.dto.PackingTaskResponse;
import com.logistics.domain.warehouse.presentation.dto.PickingTaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** 피킹·포장 작업 목록. 주문번호·상품명·위치는 각 도메인 서비스로 조회한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkTaskQueryService {

    private final PickingTaskRepository pickingRepository;
    private final PackingTaskRepository packingRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final LocationQueryService locationQueryService;

    public List<PickingTaskResponse> pickingTasks(WorkStatus status) {
        List<PickingTask> tasks = pickingRepository.findAllByStatus(status);
        Map<Long, Order> orders = orderService.getOrderMap(ids(tasks.stream().map(PickingTask::getOrderId)));
        Map<Long, Product> products = productService.getProductMap(ids(tasks.stream().map(PickingTask::getProductId)));
        Map<Long, LocationInfo> locations = locationQueryService.getLocationInfoMap();
        return tasks.stream().map(t -> PickingTaskResponse.of(t, orders.get(t.getOrderId()),
                products.get(t.getProductId()), locations.get(t.getLocationId()))).toList();
    }

    public List<PackingTaskResponse> packingTasks(WorkStatus status) {
        List<PackingTask> tasks = packingRepository.findAllByStatus(status);
        Map<Long, Order> orders = orderService.getOrderMap(ids(tasks.stream().map(PackingTask::getOrderId)));
        Set<Long> productIds = orders.values().stream().flatMap(o -> o.getItems().stream())
                .map(i -> i.getProductId()).collect(Collectors.toSet());
        Map<Long, Product> products = productService.getProductMap(productIds);
        return tasks.stream().map(t -> PackingTaskResponse.of(t, orders.get(t.getOrderId()), products)).toList();
    }

    private Set<Long> ids(java.util.stream.Stream<Long> stream) {
        return stream.collect(Collectors.toSet());
    }
}
