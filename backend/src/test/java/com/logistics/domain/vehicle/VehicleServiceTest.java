package com.logistics.domain.vehicle;

import com.logistics.domain.vehicle.application.VehicleService;
import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class VehicleServiceTest {

    @Autowired
    private VehicleService vehicleService;

    @Test
    @DisplayName("차량을 등록하면 기본 상태는 AVAILABLE이다")
    void create_defaultsToAvailable() {
        Vehicle vehicle = vehicleService.create(Vehicle.builder()
                .vehicleNumber("88아8888").vehicleType("탑차")
                .maxWeightKg(500).maxVolumeM3(3).hubDistanceKm(10).build());

        assertThat(vehicle.getId()).isNotNull();
        assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
    }

    @Test
    @DisplayName("차량 상태를 정비중으로 변경할 수 있다")
    void changeStatus_toMaintenance() {
        Vehicle vehicle = vehicleService.create(Vehicle.builder()
                .vehicleNumber("77차7777").vehicleType("탑차")
                .maxWeightKg(500).maxVolumeM3(3).hubDistanceKm(10).build());

        Vehicle updated = vehicleService.changeStatus(vehicle.getId(), VehicleStatus.MAINTENANCE);

        assertThat(updated.getStatus()).isEqualTo(VehicleStatus.MAINTENANCE);
    }
}
