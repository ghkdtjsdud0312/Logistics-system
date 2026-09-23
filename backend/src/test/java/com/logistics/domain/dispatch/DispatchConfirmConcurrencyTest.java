package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.application.DispatchConfirmService;
import com.logistics.domain.dispatch.presentation.dto.DispatchConfirmRequest;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 같은 차량에 겹치는 시간대로 동시에 배차를 확정하면 하나만 성공해야 한다 (비관적 잠금 검증).
 * - 트랜잭션이 각 스레드에서 실제로 커밋되어야 하므로 클래스 레벨 @Transactional을 사용하지 않는다.
 */
@ActiveProfiles("test")
@SpringBootTest
class DispatchConfirmConcurrencyTest {

    @Autowired
    private DispatchConfirmService dispatchConfirmService;
    @Autowired
    private DispatchTestFixtures fixtures;

    @Test
    @DisplayName("같은 차량, 겹치는 시간대에 대한 동시 배차 확정은 하나만 성공한다")
    void concurrentConfirm_onlyOneWins() throws InterruptedException {
        Long outboundA = fixtures.createOutbound("서울", 100, 1.0);
        Long outboundB = fixtures.createOutbound("인천", 100, 1.0);
        Vehicle vehicle = fixtures.createVehicle("99자9999", 500, 3.0, 10);
        Driver driverA = fixtures.createDriver("동시성A");
        Driver driverB = fixtures.createDriver("동시성B");
        LocalDateTime plannedAt = LocalDateTime.now().plusDays(2);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Boolean> taskA = confirmTask(vehicle.getId(), driverA.getId(), outboundA, plannedAt, ready, start);
        Callable<Boolean> taskB = confirmTask(vehicle.getId(), driverB.getId(), outboundB, plannedAt, ready, start);

        Future<Boolean> resultA = executor.submit(taskA);
        Future<Boolean> resultB = executor.submit(taskB);
        ready.await(5, TimeUnit.SECONDS);
        start.countDown();

        int successCount = 0;
        try {
            if (Boolean.TRUE.equals(resultA.get(10, TimeUnit.SECONDS))) successCount++;
            if (Boolean.TRUE.equals(resultB.get(10, TimeUnit.SECONDS))) successCount++;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }

        assertThat(successCount).isEqualTo(1);
    }

    private Callable<Boolean> confirmTask(Long vehicleId, Long driverId, Long outboundId, LocalDateTime plannedAt,
                                           CountDownLatch ready, CountDownLatch start) {
        return () -> {
            ready.countDown();
            start.await();
            try {
                dispatchConfirmService.confirm(new DispatchConfirmRequest(vehicleId, driverId, List.of(outboundId), plannedAt));
                return true;
            } catch (BusinessException e) {
                return false;
            }
        };
    }
}
