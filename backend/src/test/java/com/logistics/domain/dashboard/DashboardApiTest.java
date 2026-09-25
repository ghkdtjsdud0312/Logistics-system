package com.logistics.domain.dashboard;

import com.logistics.domain.dashboard.application.DashboardCache;
import com.logistics.support.TestFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DashboardApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private DashboardCache cache;
    @Autowired private TestFlow testFlow;

    @BeforeEach
    void clearCache() {
        cache.evictAll();
    }

    @Test
    @DisplayName("대시보드 API는 요약, 차량 현황, 최근 이벤트를 반환한다")
    void api() throws Exception {
        testFlow.deliveringShipment(4);

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orders").value(1))
                .andExpect(jsonPath("$.data.inDelivery").value(1))
                .andExpect(jsonPath("$.data.progress.DELIVERY").value(1));
        mockMvc.perform(get("/api/dashboard/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].total").value(1))
                .andExpect(jsonPath("$.data[0].completed").value(0));
        mockMvc.perform(get("/api/dashboard/events").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
