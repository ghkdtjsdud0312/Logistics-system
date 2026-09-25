package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.application.DispatchRegistrationService;
import com.logistics.domain.dispatch.application.RegisterDispatchCommand;
import com.logistics.domain.loading.application.LoadingService;
import com.logistics.support.TestData;
import com.logistics.support.TestFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

/** 트랜잭션 없이 실제 커밋되는 동시 배차 테스트. 끝나면 데이터를 정리한다. */
@ActiveProfiles("test")
@SpringBootTest
class DispatchConcurrencyTest {

    @Autowired private DispatchRegistrationService registrationService;
    @Autowired private LoadingService loadingService;
    @Autowired private TestData testData;
    @Autowired private TestFlow testFlow;

    @AfterEach
    void cleanUp() {
        testData.clear();
    }

    @Test
    @DisplayName("같은 Shipment를 서로 다른 차량으로 동시에 배차하면 한 건만 성공한다")
    void sameShipment_onlyOneSucceeds() throws Exception {
        Long shipmentId = loadingService.load(List.of(testFlow.packedOrder(0.5, 10))).get(0).getId();
        List<Long> vehicles = List.of(testData.vehicle(1000), testData.vehicle(1000), testData.vehicle(1000));
        ExecutorService pool = Executors.newFixedThreadPool(vehicles.size());
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();
        for (Long vehicleId : vehicles) {
            Long driverId = testData.driver();
            results.add(pool.submit(() -> {
                start.await();
                try {
                    registrationService.register(new RegisterDispatchCommand(vehicleId, driverId,
                            LocalDateTime.now(), LocalDateTime.now().plusHours(2), List.of(shipmentId)));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }));
        }
        start.countDown();
        int succeeded = 0;
        for (Future<Boolean> f : results) {
            if (f.get(30, TimeUnit.SECONDS)) succeeded++;
        }
        pool.shutdown();

        assertThat(succeeded).isEqualTo(1);
    }
}
