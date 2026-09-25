package com.logistics.domain.warehouse;

import com.logistics.domain.inventory.application.StockQueryService;
import com.logistics.domain.inventory.presentation.dto.StockResponse;
import com.logistics.domain.order.application.CreateOrderCommand;
import com.logistics.domain.order.application.CreateOrderCommand.Line;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.domain.warehouse.application.PackingService;
import com.logistics.domain.warehouse.application.PickingService;
import com.logistics.domain.warehouse.application.ReleaseService;
import com.logistics.domain.warehouse.application.WorkTaskQueryService;
import com.logistics.domain.warehouse.domain.WorkStatus;
import com.logistics.domain.warehouse.presentation.dto.PackingTaskResponse;
import com.logistics.domain.warehouse.presentation.dto.PickingTaskResponse;
import com.logistics.global.error.BusinessException;
import com.logistics.global.event.StatusChangedEvent;
import com.logistics.support.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@RecordApplicationEvents
class WarehouseWorkFlowTest {

    @Autowired private OrderService orderService;
    @Autowired private ReleaseService releaseService;
    @Autowired private PickingService pickingService;
    @Autowired private PackingService packingService;
    @Autowired private WorkTaskQueryService queryService;
    @Autowired private StockQueryService stockQueryService;
    @Autowired private TestData testData;
    @Autowired private ApplicationEvents events;

    private Order order;

    @BeforeEach
    void setUp() {
        Long productId = testData.product("WATER001", 0.5);
        List<Long> locations = testData.locations("A-01-01", "A-01-02");
        testData.stock(productId, locations.get(0), 6);
        testData.stock(productId, locations.get(1), 10);
        order = orderService.create(new CreateOrderCommand("김철수", "서울", "010", List.of(new Line(productId, 10))));
    }

    private int sum(java.util.function.ToIntFunction<StockResponse> field) {
        return stockQueryService.search(null, null, null, null).stream().mapToInt(field).sum();
    }

    @Test
    @DisplayName("출고 지시하면 예약 위치별 피킹 작업이 만들어지고 주문은 출고대기가 되며 중복 지시는 거부한다")
    void release() {
        var released = releaseService.release(order.getId());

        assertThat(released.pickingTaskCount()).isEqualTo(2);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.OUTBOUND_WAITING);
        List<PickingTaskResponse> tasks = queryService.pickingTasks(WorkStatus.WAITING);
        assertThat(tasks).extracting(PickingTaskResponse::requestedQty).containsExactlyInAnyOrder(6, 4);
        assertThat(tasks.get(0).taskNo()).startsWith("PICK-");
        assertThatThrownBy(() -> releaseService.release(order.getId())).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("피킹을 모두 끝내면 재고가 차감되고 주문이 피킹완료되며 포장 작업이 생기고 포장하면 포장완료된다")
    void pickAndPack() {
        releaseService.release(order.getId());
        var tasks = queryService.pickingTasks(null);
        Long first = tasks.stream().filter(t -> t.requestedQty() == 6).findFirst().orElseThrow().id();
        Long second = tasks.stream().filter(t -> t.requestedQty() == 4).findFirst().orElseThrow().id();

        pickingService.start(first);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PICKING);
        assertThatThrownBy(() -> pickingService.complete(first, 5)).isInstanceOf(BusinessException.class);
        pickingService.complete(first, 6);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PICKING);
        assertThat(sum(StockResponse::onHand)).isEqualTo(10);
        assertThat(sum(StockResponse::reserved)).isEqualTo(4);

        pickingService.start(second);
        pickingService.complete(second, 4);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PICKED);
        assertThat(order.getItems().get(0).getPickedQty()).isEqualTo(10);
        assertThat(sum(StockResponse::onHand)).isEqualTo(6);
        assertThat(sum(StockResponse::reserved)).isZero();
        assertThat(sum(StockResponse::available)).isEqualTo(6);

        List<PackingTaskResponse> packing = queryService.packingTasks(WorkStatus.WAITING);
        assertThat(packing).hasSize(1);
        assertThat(packing.get(0).items()).contains("× 10");
        packingService.complete(packing.get(0).id(), "BOX-001");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PACKED);
        assertThat(queryService.packingTasks(WorkStatus.COMPLETED).get(0).boxCode()).isEqualTo("BOX-001");
        assertThat(events.stream(StatusChangedEvent.class).map(StatusChangedEvent::toStatus))
                .containsSubsequence("RECEIVED", "OUTBOUND_WAITING", "PICKING", "PICKED", "PACKED");
        assertThatThrownBy(() -> packingService.complete(packing.get(0).id(), "BOX-002"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("출고 지시 전이나 시작 전에는 피킹을 완료할 수 없다")
    void guardOrder() {
        assertThatThrownBy(() -> releaseService.release(9999L)).isInstanceOf(BusinessException.class);
        releaseService.release(order.getId());
        Long taskId = queryService.pickingTasks(null).get(0).id();

        assertThatThrownBy(() -> pickingService.complete(taskId, 4)).isInstanceOf(BusinessException.class);
    }
}
