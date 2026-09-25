package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchRepository;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.driver.domain.DriverStatus;
import com.logistics.domain.loading.application.ShipmentService;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentStatus;
import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.application.OrderShippingService;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.vehicle.application.VehicleService;
import com.logistics.domain.vehicle.domain.CapacityValidator;
import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.event.StatusChangedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 배차 등록: Shipment·차량·기사를 검증하고 묶는다. 하나라도 실패하면 전체가 롤백된다. */
@Service
@RequiredArgsConstructor
@Transactional
public class DispatchRegistrationService {

    private final DispatchRepository dispatchRepository;
    private final ShipmentService shipmentService;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final OrderService orderService;
    private final ProductService productService;
    private final OrderShippingService orderShippingService;
    private final StatusChangedEventPublisher eventPublisher;

    public Dispatch register(RegisterDispatchCommand cmd) {
        List<Shipment> shipments = shipmentService.getAllForUpdate(cmd.shipmentIds());
        if (shipments.stream().anyMatch(s -> s.getStatus() != ShipmentStatus.LOADED || s.getDispatchId() != null)) {
            throw new BusinessException(ErrorCode.SHIPMENT_ALREADY_DISPATCHED);
        }
        Vehicle vehicle = vehicleService.getVehicleForUpdate(cmd.vehicleId());
        Driver driver = driverService.getDriverForUpdate(cmd.driverId());
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE || dispatchRepository.existsActiveByVehicleId(vehicle.getId())) {
            throw new BusinessException(ErrorCode.VEHICLE_UNAVAILABLE);
        }
        if (driver.getStatus() != DriverStatus.AVAILABLE || dispatchRepository.existsActiveByDriverId(driver.getId())) {
            throw new BusinessException(ErrorCode.DRIVER_UNAVAILABLE);
        }
        double weight = totalWeight(shipments);
        if (!CapacityValidator.fits(vehicle, weight)) {
            throw new BusinessException(ErrorCode.VEHICLE_OVERLOAD);
        }
        Dispatch dispatch = dispatchRepository.save(new Dispatch(vehicle.getId(), driver.getId(),
                cmd.plannedStartAt(), cmd.plannedArrivalAt(), weight));
        dispatch.assignNo();
        for (Shipment shipment : shipments) {
            shipment.assignTo(dispatch.getId());
            orderShippingService.markDispatched(shipment.getOrderId());
        }
        eventPublisher.publish("DISPATCH", dispatch.getId(), dispatch.getDispatchNo(), null,
                "REGISTER", null, DispatchStatus.REGISTERED.name());
        return dispatch;
    }

    private double totalWeight(List<Shipment> shipments) {
        Map<Long, Order> orders = orderService.getOrderMap(
                shipments.stream().map(Shipment::getOrderId).collect(Collectors.toSet()));
        Map<Long, Product> products = productService.getProductMap(orders.values().stream()
                .flatMap(o -> o.getItems().stream()).map(i -> i.getProductId()).collect(Collectors.toSet()));
        return orders.values().stream().mapToDouble(o -> OrderWeights.of(o, products)).sum();
    }
}
