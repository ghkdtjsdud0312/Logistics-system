package com.logistics.domain.dispatch;

import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.driver.domain.DriverRepository;
import com.logistics.domain.inbound.application.InboundService;
import com.logistics.domain.inbound.presentation.dto.InboundCreateRequest;
import com.logistics.domain.outbound.application.OutboundService;
import com.logistics.domain.outbound.domain.Outbound;
import com.logistics.domain.outbound.presentation.dto.OutboundCreateRequest;
import com.logistics.domain.outbound.presentation.dto.OutboundItemRequest;
import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.domain.vehicle.domain.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/** 배차 테스트 공통 픽스처 생성 도우미 */
@Component
public class DispatchTestFixtures {

    @Autowired
    private InboundService inboundService;
    @Autowired
    private OutboundService outboundService;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private DriverRepository driverRepository;

    public Long createOutbound(String destination, double weightKg, double volumeM3) {
        return createOutbound(destination, weightKg, volumeM3, 37.50, 127.00);
    }

    public Long createOutbound(String destination, double weightKg, double volumeM3, double latitude, double longitude) {
        var inbound = inboundService.createInbound(new InboundCreateRequest("품목", 10, "A-01"));
        inboundService.startInbound(inbound.getId());
        var completed = inboundService.completeInbound(inbound.getId(), 10);

        Outbound outbound = outboundService.createOutbound(new OutboundCreateRequest(
                destination, latitude, longitude,
                List.of(new OutboundItemRequest(completed.getId(), 1, weightKg, volumeM3))));
        return outbound.getId();
    }

    public Vehicle createVehicle(String vehicleNumber, double maxWeightKg, double maxVolumeM3, double hubDistanceKm) {
        return vehicleRepository.save(Vehicle.builder()
                .vehicleNumber(vehicleNumber)
                .vehicleType("탑차")
                .maxWeightKg(maxWeightKg)
                .maxVolumeM3(maxVolumeM3)
                .hubDistanceKm(hubDistanceKm)
                .build());
    }

    public Driver createDriver(String name) {
        return driverRepository.save(Driver.builder().name(name).build());
    }
}
