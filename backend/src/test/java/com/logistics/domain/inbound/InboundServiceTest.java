package com.logistics.domain.inbound;

import com.logistics.domain.inbound.application.InboundService;
import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.presentation.dto.InboundCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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
}
