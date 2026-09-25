package com.logistics.support;

import com.logistics.domain.order.application.CreateOrderCommand;
import com.logistics.domain.order.application.CreateOrderCommand.Line;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.warehouse.application.PackingService;
import com.logistics.domain.warehouse.application.PickingService;
import com.logistics.domain.warehouse.application.ReleaseService;
import com.logistics.domain.warehouse.application.WorkTaskQueryService;
import com.logistics.domain.warehouse.domain.WorkStatus;
import com.logistics.domain.warehouse.presentation.dto.PackingTaskResponse;
import com.logistics.domain.warehouse.presentation.dto.PickingTaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** 테스트에서 주문을 원하는 단계까지 진행시키는 도우미 */
@Component
@RequiredArgsConstructor
public class TestFlow {

    private final TestData testData;
    private final OrderService orderService;
    private final ReleaseService releaseService;
    private final PickingService pickingService;
    private final PackingService packingService;
    private final WorkTaskQueryService workTaskQueryService;

    /** 상품·재고·주문을 만들고 포장완료까지 진행한 뒤 주문 ID를 반환한다. */
    public Long packedOrder(double unitWeightKg, int quantity) {
        long seed = System.nanoTime();
        Long productId = testData.product("P" + seed, unitWeightKg);
        testData.stock(productId, testData.locations("L" + seed).get(0), quantity);
        Long orderId = orderService.create(new CreateOrderCommand("김철수", "서울시 강남구", "010",
                List.of(new Line(productId, quantity)))).getId();
        releaseService.release(orderId);
        for (PickingTaskResponse task : workTaskQueryService.pickingTasks(WorkStatus.WAITING)) {
            pickingService.start(task.id());
            pickingService.complete(task.id(), task.requestedQty());
        }
        for (PackingTaskResponse task : workTaskQueryService.packingTasks(WorkStatus.WAITING)) {
            packingService.complete(task.id(), "BOX-" + seed);
        }
        return orderId;
    }
}
