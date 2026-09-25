package com.logistics.domain.order;

import com.logistics.domain.inventory.application.StockQueryService;
import com.logistics.domain.inventory.application.StockReservationService;
import com.logistics.domain.inventory.presentation.dto.StockResponse;
import com.logistics.domain.order.application.CreateOrderCommand;
import com.logistics.domain.order.application.CreateOrderCommand.Line;
import com.logistics.domain.order.application.OrderQueryService;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@RecordApplicationEvents
class OrderServiceTest {

    @Autowired private OrderService orderService;
    @Autowired private OrderQueryService queryService;
    @Autowired private StockReservationService reservationService;
    @Autowired private StockQueryService stockQueryService;
    @Autowired private TestData testData;
    @Autowired private ApplicationEvents events;

    private Long waterId;
    private List<Long> locations;

    @BeforeEach
    void setUp() {
        waterId = testData.product("WATER001", 0.5);
        locations = testData.locations("A-01-01", "A-01-02");
        testData.stock(waterId, locations.get(0), 6);
        testData.stock(waterId, locations.get(1), 10);
    }

    private Order order(int quantity) {
        return orderService.create(new CreateOrderCommand("김철수", "서울시 강남구", "010-0000-0000",
                List.of(new Line(waterId, quantity))));
    }

    @Test
    @DisplayName("주문을 생성하면 ORD 번호가 붙고 주문접수 상태이며 가용재고가 줄고 이벤트가 발행된다")
    void create_reservesStock() {
        Order order = order(10);

        assertThat(order.getOrderNo()).isEqualTo("ORD-%03d".formatted(order.getId()));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.RECEIVED);
        List<StockResponse> stocks = stockQueryService.search(null, null, null, null);
        assertThat(stocks.stream().mapToInt(StockResponse::onHand).sum()).isEqualTo(16);
        assertThat(stocks.stream().mapToInt(StockResponse::reserved).sum()).isEqualTo(10);
        assertThat(stocks.stream().mapToInt(StockResponse::available).sum()).isEqualTo(6);
        assertThat(events.stream(StatusChangedEvent.class).map(StatusChangedEvent::toStatus))
                .contains("RECEIVED");
    }

    @Test
    @DisplayName("예약은 위치 코드 순으로 배분되어 여러 위치에 나뉜다")
    void reserve_splitsAcrossLocations() {
        Order order = order(10);

        var reservations = reservationService.getReservations(order.getId());

        assertThat(reservations).hasSize(2);
        assertThat(reservations.get(0).getLocationId()).isEqualTo(locations.get(0));
        assertThat(reservations.get(0).getQuantity()).isEqualTo(6);
        assertThat(reservations.get(1).getLocationId()).isEqualTo(locations.get(1));
        assertThat(reservations.get(1).getQuantity()).isEqualTo(4);
    }

    @Test
    @DisplayName("가용재고와 같은 수량은 예약되고 초과하면 INSUFFICIENT_STOCK으로 거절되며 예약이 늘지 않는다")
    void insufficientStock() {
        order(16);
        assertThatThrownBy(() -> order(1)).isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_STOCK);

        assertThat(stockQueryService.search(null, null, null, null).stream()
                .mapToInt(StockResponse::reserved).sum()).isEqualTo(16);
    }

    @Test
    @DisplayName("없는 상품이 포함된 주문은 거절한다")
    void unknownProduct() {
        assertThatThrownBy(() -> orderService.create(new CreateOrderCommand("김철수", "서울", "010",
                List.of(new Line(9999L, 1))))).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("주문 목록은 고객명·상태·일자로 검색하고 상세는 수량과 타임라인을 돌려준다")
    void searchAndDetail() {
        Order order = order(3);
        LocalDate today = LocalDate.now();

        assertThat(queryService.search(new OrderSearchCriteria(null, "철수", OrderStatus.RECEIVED, today, today, 0, 20)))
                .hasSize(1).first().satisfies(row -> {
                    assertThat(row.orderNo()).isEqualTo(order.getOrderNo());
                    assertThat(row.quantity()).isEqualTo(3);
                    assertThat(row.deliveryStatus()).isNull();
                });
        assertThat(queryService.search(new OrderSearchCriteria(null, "없는고객", null, null, null, 0, 20))).isEmpty();
        assertThat(queryService.search(new OrderSearchCriteria(null, null, OrderStatus.PICKED, null, null, 0, 20))).isEmpty();

        var detail = queryService.getDetail(order.getId());
        assertThat(detail.items().get(0).orderedQty()).isEqualTo(3);
        assertThat(detail.items().get(0).pickedQty()).isZero();
        assertThat(detail.timeline()).hasSize(7);
    }
}
