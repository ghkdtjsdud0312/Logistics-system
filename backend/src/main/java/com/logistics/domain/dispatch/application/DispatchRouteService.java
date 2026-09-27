package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.RouteOptimizer;
import com.logistics.domain.dispatch.presentation.dto.RouteView;
import com.logistics.domain.loading.application.ShipmentService;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.master.application.WarehouseService;
import com.logistics.domain.master.domain.Warehouse;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.Order;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.geocoding.Coordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/** 적재중 배차의 방문 순서 지정과 경로 최적화, 총 이동 거리 계산(직선거리 근사). */
@Service
@RequiredArgsConstructor
@Transactional
public class DispatchRouteService {

    private final DispatchService dispatchService;
    private final ShipmentService shipmentService;
    private final WarehouseService warehouseService;
    private final OrderService orderService;

    public Dispatch reorder(Long dispatchId, List<Long> orderedShipmentIds) {
        Dispatch dispatch = dispatchService.get(dispatchId);
        dispatch.requireLoading();
        List<Shipment> shipments = shipmentService.getByDispatchId(dispatchId);
        if (orderedShipmentIds.size() != shipments.size()
                || !new HashSet<>(orderedShipmentIds).equals(shipments.stream().map(Shipment::getId).collect(java.util.stream.Collectors.toSet()))) {
            throw new BusinessException(ErrorCode.INVALID_ROUTE_ORDER);
        }
        Map<Long, Shipment> byId = shipments.stream().collect(java.util.stream.Collectors.toMap(Shipment::getId, s -> s));
        int stop = 1;
        for (Long id : orderedShipmentIds) {
            byId.get(id).assignStopOrder(stop++);
        }
        return dispatch;
    }

    /** 방문 순서를 최적화해 저장하고, 좌표를 얻지 못해 맨 뒤로 보낸 배송 ID를 돌려준다. */
    public List<Long> optimize(Long dispatchId) {
        Dispatch dispatch = dispatchService.get(dispatchId);
        dispatch.requireLoading();
        Coordinate origin = originOf(dispatch, true);
        List<Shipment> shipments = shipmentService.getByDispatchId(dispatchId);
        Map<Long, Order> orders = orderService.getLocatedOrderMap(shipments.stream().map(Shipment::getOrderId).toList());
        List<Shipment> located = new ArrayList<>();
        List<Shipment> unlocated = new ArrayList<>();
        for (Shipment shipment : shipments) {
            (orders.get(shipment.getOrderId()).hasCoordinate() ? located : unlocated).add(shipment);
        }
        List<Coordinate> stops = located.stream().map(s -> coordinateOf(orders.get(s.getOrderId()))).toList();
        int stop = 1;
        for (int index : RouteOptimizer.optimize(origin, stops)) {
            located.get(index).assignStopOrder(stop++);
        }
        for (Shipment shipment : unlocated) {
            shipment.assignStopOrder(stop++);
        }
        return unlocated.stream().map(Shipment::getId).toList();
    }

    /** 적재중 배차의 출발지를 지정·변경한다. 좌표가 없으면 최적화 시점에 다시 변환을 시도한다. */
    public Dispatch changeOrigin(Long dispatchId, Long warehouseId) {
        Dispatch dispatch = dispatchService.get(dispatchId);
        dispatch.requireLoading();
        warehouseService.get(warehouseId);
        dispatch.changeWarehouse(warehouseId);
        return dispatch;
    }

    /** 응답용 경로 정보. 조회만 하며 좌표 변환은 하지 않는다. 거리는 출발지와 모든 배송지 좌표가 있을 때만 계산한다. */
    @Transactional(readOnly = true)
    public RouteView view(Dispatch dispatch, List<Shipment> orderedShipments) {
        Warehouse warehouse = dispatch.getWarehouseId() == null ? null : warehouseService.get(dispatch.getWarehouseId());
        Map<Long, Order> orders = orderService.getOrderMap(orderedShipments.stream().map(Shipment::getOrderId).toList());
        List<RouteView.Stop> stops = orderedShipments.stream().map(s -> stopOf(s, orders.get(s.getOrderId()))).toList();
        RouteView.Origin origin = warehouse == null ? null
                : new RouteView.Origin(warehouse.getName(), warehouse.getLatitude(), warehouse.getLongitude());
        return new RouteView(origin, stops, distanceKm(warehouse, stops));
    }

    private Double distanceKm(Warehouse warehouse, List<RouteView.Stop> stops) {
        if (warehouse == null || warehouse.getLatitude() == null || stops.isEmpty()
                || stops.stream().anyMatch(s -> s.latitude() == null)) {
            return null;
        }
        List<Coordinate> points = stops.stream().map(s -> new Coordinate(s.latitude(), s.longitude())).toList();
        double km = RouteOptimizer.totalKm(new Coordinate(warehouse.getLatitude(), warehouse.getLongitude()), points);
        return Math.round(km * 10) / 10.0;
    }

    private RouteView.Stop stopOf(Shipment shipment, Order order) {
        return new RouteView.Stop(shipment.getId(), order.getOrderNo(), order.getAddress(),
                order.getLatitude(), order.getLongitude());
    }

    private Coordinate originOf(Dispatch dispatch, boolean required) {
        Warehouse warehouse = dispatch.getWarehouseId() == null ? null
                : (required ? warehouseService.getLocated(dispatch.getWarehouseId()) : warehouseService.get(dispatch.getWarehouseId()));
        if (warehouse == null || warehouse.getLatitude() == null) {
            if (required) {
                throw new BusinessException(ErrorCode.ROUTE_ORIGIN_MISSING);
            }
            return null;
        }
        return new Coordinate(warehouse.getLatitude(), warehouse.getLongitude());
    }

    private Coordinate coordinateOf(Order order) {
        return new Coordinate(order.getLatitude(), order.getLongitude());
    }
}
