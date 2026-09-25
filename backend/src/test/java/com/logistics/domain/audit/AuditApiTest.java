package com.logistics.domain.audit;

import com.logistics.domain.audit.application.AuditService;
import com.logistics.global.event.StatusChangedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuditApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AuditService auditService;

    @Test
    @DisplayName("감사로그 API는 최신순으로 반환하고 조건으로 검색한다")
    void search_api() throws Exception {
        auditService.record(new StatusChangedEvent("e1", "ORDER", 1L, "ORD-001", 1L, "DELIVER",
                "IN_DELIVERY", "DELIVERED", "홍길동", Instant.now()));
        auditService.record(new StatusChangedEvent("e2", "ORDER", 1L, "ORD-001", 1L, "DELIVERY_START",
                "DISPATCHED", "IN_DELIVERY", "관리자", Instant.now().minusSeconds(60)));

        mockMvc.perform(get("/api/audit-logs").param("target", "ORD-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].actor").value("홍길동"))
                .andExpect(jsonPath("$.data[0].description").value("배송 완료"));
        mockMvc.perform(get("/api/audit-logs").param("actor", "관리자").param("action", "DELIVERY_START"))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].toStatus").value("IN_DELIVERY"));
    }
}
