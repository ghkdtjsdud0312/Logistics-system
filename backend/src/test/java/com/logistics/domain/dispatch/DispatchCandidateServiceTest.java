package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.application.DispatchCandidateService;
import com.logistics.domain.dispatch.presentation.dto.DispatchCandidateRequest;
import com.logistics.domain.dispatch.presentation.dto.DispatchCandidateResponse;
import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.domain.vehicle.domain.VehicleRepository;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DispatchCandidateServiceTest {

    @Autowired
    private DispatchCandidateService dispatchCandidateService;
    @Autowired
    private DispatchTestFixtures fixtures;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    @DisplayName("중량과 부피를 모두 만족하는 차량만 후보로 추천하고 부적합 차량은 사유와 함께 제외한다")
    void findCandidates_filtersByCapacityAndStatus() {
        Long outboundId = fixtures.createOutbound("서울", 300, 2.0);
        Vehicle fits = fixtures.createVehicle("11가1111", 500, 3.0, 10);
        Vehicle overVolume = fixtures.createVehicle("22나2222", 500, 1.5, 10);
        Vehicle maintenance = fixtures.createVehicle("33다3333", 500, 3.0, 10);
        maintenance.changeStatus(VehicleStatus.MAINTENANCE);
        vehicleRepository.save(maintenance);

        DispatchCandidateResponse response = dispatchCandidateService.findCandidates(
                new DispatchCandidateRequest(List.of(outboundId), LocalDateTime.now().plusHours(1)));

        assertThat(response.candidates()).extracting("vehicleId").containsExactly(fits.getId());
        assertThat(response.excluded()).extracting("vehicleId")
                .containsExactlyInAnyOrder(overVolume.getId(), maintenance.getId());
        assertThat(response.excluded()).extracting("reasonCode")
                .containsExactlyInAnyOrder("OVER_CAPACITY", "VEHICLE_UNAVAILABLE");
    }

    @Test
    @DisplayName("같은 입력이면 잔여 적재율 기준 후보 정렬 결과가 항상 같다")
    void findCandidates_sortIsDeterministic() {
        Long outboundId = fixtures.createOutbound("서울", 100, 1.0);
        Vehicle near = fixtures.createVehicle("44라4444", 200, 2.0, 5);
        Vehicle far = fixtures.createVehicle("55마5555", 200, 2.0, 80);

        DispatchCandidateRequest request = new DispatchCandidateRequest(List.of(outboundId), LocalDateTime.now().plusHours(1));
        List<Long> firstOrder = dispatchCandidateService.findCandidates(request).candidates().stream()
                .map(c -> c.vehicleId()).toList();
        List<Long> secondOrder = dispatchCandidateService.findCandidates(request).candidates().stream()
                .map(c -> c.vehicleId()).toList();

        assertThat(firstOrder).isEqualTo(secondOrder).containsExactly(near.getId(), far.getId());
    }
}
