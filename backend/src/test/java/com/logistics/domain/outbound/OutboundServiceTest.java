package com.logistics.domain.outbound;

import com.logistics.domain.inbound.application.InboundService;
import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.presentation.dto.InboundCreateRequest;
import com.logistics.domain.outbound.application.OutboundService;
import com.logistics.domain.outbound.domain.Outbound;
import com.logistics.domain.outbound.presentation.dto.OutboundCreateRequest;
import com.logistics.domain.outbound.presentation.dto.OutboundItemRequest;
import com.logistics.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OutboundServiceTest {

    @Autowired
    private InboundService inboundService;
    @Autowired
    private OutboundService outboundService;

    private Long completeInbound(String itemName, int quantity, int inspectedQuantity) {
        Inbound inbound = inboundService.createInbound(
                new InboundCreateRequest(itemName, quantity, "A-01"));
        inboundService.startInbound(inbound.getId());
        return inboundService.completeInbound(inbound.getId(), inspectedQuantity).getId();
    }

    @Test
    @DisplayName("검수 완료된 여러 입고건을 합쳐 출고 계획을 만들 수 있다")
    void createOutbound_withMultipleInbounds() {
        Long inbound1 = completeInbound("파렛트 A", 10, 10);
        Long inbound2 = completeInbound("파렛트 B", 5, 5);

        Outbound outbound = outboundService.createOutbound(new OutboundCreateRequest(
                "서울 물류센터",
                List.of(new OutboundItemRequest(inbound1, 6), new OutboundItemRequest(inbound2, 5))));

        assertThat(outbound.getId()).isNotNull();
        assertThat(outbound.totalQuantity()).isEqualTo(11);
    }

    @Test
    @DisplayName("검수 완료되지 않은 입고는 출고 대상으로 선택할 수 없다")
    void createOutbound_withNotCompletedInbound_throws() {
        Inbound requested = inboundService.createInbound(
                new InboundCreateRequest("파렛트 C", 10, "A-02"));

        assertThatThrownBy(() -> outboundService.createOutbound(new OutboundCreateRequest(
                "부산 물류센터", List.of(new OutboundItemRequest(requested.getId(), 1)))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("가용 수량을 초과해 출고를 요청하면 예외가 발생한다")
    void createOutbound_exceedsAvailableQuantity_throws() {
        Long inboundId = completeInbound("파렛트 D", 10, 10);

        assertThatThrownBy(() -> outboundService.createOutbound(new OutboundCreateRequest(
                "대전 물류센터", List.of(new OutboundItemRequest(inboundId, 11)))))
                .isInstanceOf(BusinessException.class);
    }
}
