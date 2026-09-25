package com.logistics.domain.inbound;

import com.logistics.domain.inbound.application.InboundService;
import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundStatus;
import com.logistics.domain.inventory.application.StockQueryService;
import com.logistics.domain.inventory.presentation.dto.StockResponse;
import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.application.WarehouseService;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.master.domain.Warehouse;
import com.logistics.global.error.BusinessException;
import com.logistics.global.event.StatusChangedEvent;
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
class InboundServiceTest {

    @Autowired private InboundService inboundService;
    @Autowired private ProductService productService;
    @Autowired private WarehouseService warehouseService;
    @Autowired private StockQueryService stockQueryService;
    @Autowired private ApplicationEvents events;

    private Long productId;
    private Long locationId;

    @BeforeEach
    void setUp() {
        productId = productService.create(Product.builder()
                .code("WATER001").name("생수 500ml").unit("EA").unitWeightKg(0.5).build()).getId();
        Warehouse warehouse = warehouseService.createWarehouse("A", "A창고");
        Long zoneId = warehouseService.addZone(warehouse.getId(), "A01", "A구역").getId();
        locationId = warehouseService.addLocation(zoneId, "A-01-01").getId();
    }

    private Inbound newInbound() {
        return inboundService.create("거래처1", productId, 100, LocalDate.of(2026, 9, 25));
    }

    @Test
    @DisplayName("입고예정 등록 시 업무번호가 부여되고 상태는 EXPECTED이다")
    void create() {
        Inbound inbound = newInbound();

        assertThat(inbound.getInboundNo()).isEqualTo("IN-%03d".formatted(inbound.getId()));
        assertThat(inbound.getStatus()).isEqualTo(InboundStatus.EXPECTED);
    }

    @Test
    @DisplayName("입고완료 → 적치완료 하면 위치 재고가 입고수량만큼 늘고 상태 변경 이벤트가 발행된다")
    void receive_andPutaway_increasesStock() {
        Inbound inbound = newInbound();

        inboundService.receive(inbound.getId());
        assertThat(inbound.getStatus()).isEqualTo(InboundStatus.PUTAWAY_WAITING);
        inboundService.putaway(inbound.getId(), locationId);

        assertThat(inbound.getStatus()).isEqualTo(InboundStatus.PUTAWAY_DONE);
        List<StockResponse> stocks = stockQueryService.search(null, null, null, null);
        assertThat(stocks).hasSize(1);
        assertThat(stocks.get(0).onHand()).isEqualTo(100);
        assertThat(stocks.get(0).reserved()).isZero();
        assertThat(stocks.get(0).available()).isEqualTo(100);
        assertThat(stocks.get(0).locationCode()).isEqualTo("A-01-01");
        assertThat(events.stream(StatusChangedEvent.class).map(StatusChangedEvent::toStatus))
                .containsExactly("EXPECTED", "RECEIVED", "PUTAWAY_WAITING", "PUTAWAY_DONE");
    }

    @Test
    @DisplayName("입고완료 전 적치, 중복 입고완료, 중복 적치는 거부하고 재고는 늘지 않는다")
    void invalidTransitions() {
        Inbound inbound = newInbound();

        assertThatThrownBy(() -> inboundService.putaway(inbound.getId(), locationId))
                .isInstanceOf(BusinessException.class);
        inboundService.receive(inbound.getId());
        assertThatThrownBy(() -> inboundService.receive(inbound.getId())).isInstanceOf(BusinessException.class);
        inboundService.putaway(inbound.getId(), locationId);
        assertThatThrownBy(() -> inboundService.putaway(inbound.getId(), locationId))
                .isInstanceOf(BusinessException.class);

        assertThat(stockQueryService.search(null, null, null, null).get(0).onHand()).isEqualTo(100);
    }

    @Test
    @DisplayName("없는 상품·위치, 0 이하 수량은 거부한다")
    void invalidReferences() {
        assertThatThrownBy(() -> inboundService.create("거래처", 9999L, 10, LocalDate.now()))
                .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> inboundService.create("거래처", productId, 0, LocalDate.now()))
                .isInstanceOf(BusinessException.class);
        Inbound inbound = newInbound();
        inboundService.receive(inbound.getId());
        assertThatThrownBy(() -> inboundService.putaway(inbound.getId(), 9999L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("같은 위치에 두 번 적치하면 재고가 누적된다")
    void putaway_accumulates() {
        for (int i = 0; i < 2; i++) {
            Inbound inbound = newInbound();
            inboundService.receive(inbound.getId());
            inboundService.putaway(inbound.getId(), locationId);
        }

        assertThat(stockQueryService.search(null, null, "WATER", null).get(0).onHand()).isEqualTo(200);
    }
}
