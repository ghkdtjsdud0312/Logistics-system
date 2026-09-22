package com.logistics.domain.inbound;

import com.logistics.domain.inbound.application.InboundService;
import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundStatus;
import com.logistics.domain.inbound.presentation.dto.InboundCreateRequest;
import com.logistics.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class InboundServiceTest {

    @Autowired
    private InboundService inboundService;

    @Test
    @DisplayName("입고를 등록하면 REQUESTED 상태로 저장된다")
    void createInbound() {
        InboundCreateRequest request = new InboundCreateRequest("파렛트 A", 10, "A-01");

        Inbound inbound = inboundService.createInbound(request);

        assertThat(inbound.getId()).isNotNull();
        assertThat(inbound.getItemName()).isEqualTo("파렛트 A");
    }

    @Test
    @DisplayName("입고 처리 시작 후 검수 완료하면 COMPLETED 상태와 검수 수량이 저장된다")
    void startAndCompleteInbound() {
        Inbound inbound = inboundService.createInbound(
                new InboundCreateRequest("파렛트 B", 10, "A-02"));

        inboundService.startInbound(inbound.getId());
        Inbound completed = inboundService.completeInbound(inbound.getId(), 9);

        assertThat(completed.getStatus()).isEqualTo(InboundStatus.COMPLETED);
        assertThat(completed.getInspectedQuantity()).isEqualTo(9);
    }

    @Test
    @DisplayName("입고 처리를 거치지 않고 바로 검수 완료하면 예외가 발생한다")
    void completeWithoutStart_throws() {
        Inbound inbound = inboundService.createInbound(
                new InboundCreateRequest("파렛트 C", 5, "A-03"));

        assertThatThrownBy(() -> inboundService.completeInbound(inbound.getId(), 5))
                .isInstanceOf(BusinessException.class);
    }
}
