package com.logistics.domain.order;

import com.logistics.domain.inventory.application.StockQueryService;
import com.logistics.domain.inventory.presentation.dto.StockResponse;
import com.logistics.domain.order.application.CreateOrderCommand;
import com.logistics.domain.order.application.CreateOrderCommand.Line;
import com.logistics.domain.order.application.OrderQueryService;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import com.logistics.support.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

/** 트랜잭션 없이 실제 커밋되는 동시 주문 테스트. 끝나면 데이터를 정리한다. */
@ActiveProfiles("test")
@SpringBootTest
class OrderConcurrencyTest {

    private static final int THREADS = 10;
    private static final int ORDER_QTY = 20;

    @Autowired private OrderService orderService;
    @Autowired private OrderQueryService queryService;
    @Autowired private StockQueryService stockQueryService;
    @Autowired private TestData testData;

    @AfterEach
    void cleanUp() {
        testData.clear();
    }

    @Test
    @DisplayName("재고 100에 20개 주문 10건을 동시에 넣어도 가용재고를 초과해 예약되지 않는다")
    void concurrentOrders_neverOverReserve() throws Exception {
        Long productId = testData.product("CONC001", 1);
        testData.stock(productId, testData.locations("C-01-01").get(0), 100);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            results.add(pool.submit(() -> {
                start.await();
                try {
                    orderService.create(new CreateOrderCommand("고객", "주소", "010", List.of(new Line(productId, ORDER_QTY))));
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

        StockResponse stock = stockQueryService.search(null, null, "CONC", null).get(0);
        assertThat(succeeded).isBetween(1, 5);
        assertThat(stock.reserved()).isEqualTo(succeeded * ORDER_QTY).isLessThanOrEqualTo(stock.onHand());
        assertThat(queryService.search(new OrderSearchCriteria(null, null, null, null, null, 0, 50))).hasSize(succeeded);
    }
}
