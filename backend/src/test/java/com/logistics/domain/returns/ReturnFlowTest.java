package com.logistics.domain.returns;

import com.logistics.domain.delivery.application.DeliveryService;
import com.logistics.domain.inventory.application.StockQueryService;
import com.logistics.domain.loading.domain.FailReason;
import com.logistics.domain.returns.application.ReturnQueryService;
import com.logistics.domain.returns.application.ReturnService;
import com.logistics.domain.returns.domain.ReturnStatus;
import com.logistics.domain.returns.presentation.dto.ReturnResponse;
import com.logistics.global.error.BusinessException;
import com.logistics.global.event.StatusChangedEvent;
import com.logistics.support.TestData;
import com.logistics.support.TestFlow;
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
class ReturnFlowTest {

    @Autowired private DeliveryService deliveryService;
    @Autowired private ReturnService returnService;
    @Autowired private ReturnQueryService queryService;
    @Autowired private StockQueryService stockQueryService;
    @Autowired private TestData testData;
    @Autowired private TestFlow testFlow;
    @Autowired private ApplicationEvents events;

    private int stockAt(String locationCode) {
        return stockQueryService.search(null, null, null, null).stream()
                .filter(s -> s.locationCode().equals(locationCode)).mapToInt(s -> s.onHand()).sum();
    }

    private Long failAndGetReturnId(FailReason reason) {
        deliveryService.fail(testFlow.deliveringShipment(5), reason, "상세");
        List<ReturnResponse> requested = queryService.getList(ReturnStatus.REQUESTED);
        assertThat(requested).hasSize(1);
        return requested.get(0).id();
    }

    @Test
    @DisplayName("배송 실패 시 반품이 정확히 1건 자동 생성되고 회수 → 반품입고 시 위치 재고가 복구된다")
    void failure_createsReturn_andRestocks() {
        Long returnId = failAndGetReturnId(FailReason.CUSTOMER_ABSENT);
        ReturnResponse created = queryService.getDetail(returnId);
        assertThat(created.returnNo()).isEqualTo("RTN-%03d".formatted(returnId));
        assertThat(created.quantity()).isEqualTo(5);
        assertThat(created.items()).contains("× 5");

        Long locationId = testData.locations("R-01-01").get(0);
        returnService.collect(returnId);
        returnService.collected(returnId);
        assertThat(stockAt("R-01-01")).isZero();
        returnService.receive(returnId, locationId);
        assertThat(stockAt("R-01-01")).isEqualTo(5);
        returnService.complete(returnId);

        assertThat(queryService.getDetail(returnId).status()).isEqualTo(ReturnStatus.COMPLETED);
        assertThat(events.stream(StatusChangedEvent.class).filter(e -> e.targetType().equals("RETURN"))
                .map(StatusChangedEvent::toStatus))
                .containsExactly("REQUESTED", "COLLECTING", "COLLECTED", "RETURN_RECEIVED", "COMPLETED");
    }

    @Test
    @DisplayName("상품 파손 사유는 반품입고해도 재고를 복구하지 않고 위치 없이도 입고된다")
    void damaged_noRestock() {
        Long returnId = failAndGetReturnId(FailReason.DAMAGED);
        Long locationId = testData.locations("R-02-01").get(0);

        returnService.collect(returnId);
        returnService.collected(returnId);
        returnService.receive(returnId, locationId);

        assertThat(stockAt("R-02-01")).isZero();
        assertThat(queryService.getDetail(returnId).status()).isEqualTo(ReturnStatus.RETURN_RECEIVED);
    }

    @Test
    @DisplayName("위치 없는 반품입고와 순서를 건너뛴 처리는 거부하고 없는 반품은 404 대상이다")
    void invalid() {
        Long returnId = failAndGetReturnId(FailReason.REFUSED);

        assertThatThrownBy(() -> returnService.receive(returnId, 1L)).isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> returnService.complete(returnId)).isInstanceOf(BusinessException.class);
        returnService.collect(returnId);
        returnService.collected(returnId);
        assertThatThrownBy(() -> returnService.receive(returnId, null)).isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> returnService.get(999999L)).isInstanceOf(BusinessException.class);
    }
}
