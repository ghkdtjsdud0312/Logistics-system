package com.logistics.domain.delivery;

import com.jayway.jsonpath.JsonPath;
import com.logistics.support.TestData;
import com.logistics.support.TestFlow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeliveryApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TestData testData;
    @Autowired private TestFlow testFlow;

    private String read(String json, String path) {
        return String.valueOf((Object) JsonPath.read(json, path));
    }

    private String postJson(String path, String body) throws Exception {
        return mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("배송현황 조회 → 배송 완료(수량 불일치 422 후 성공) → 배송 실패 사유 검증 → 주문 상세의 배송 정보")
    void api_flow() throws Exception {
        Long deliveredOrder = testFlow.packedOrder(0.5, 10);
        Long failedOrder = testFlow.packedOrder(0.5, 2);
        String loaded = postJson("/api/loadings", "{\"orderIds\":[" + deliveredOrder + "," + failedOrder + "]}");
        String s1 = read(loaded, "$.data[0].id");
        String s2 = read(loaded, "$.data[1].id");
        String dispatch = postJson("/api/dispatches", "{\"vehicleId\":" + testData.vehicle(1000) + ",\"driverId\":"
                + testData.driver() + ",\"plannedStartAt\":\"2026-09-25T13:00:00\",\"plannedArrivalAt\":\"2026-09-25T15:00:00\","
                + "\"shipmentIds\":[" + s1 + "," + s2 + "]}");
        mockMvc.perform(patch("/api/dispatches/" + read(dispatch, "$.data.id") + "/start")).andExpect(status().isOk());

        mockMvc.perform(get("/api/delivery-status"))
                .andExpect(jsonPath("$.data[0].total").value(2))
                .andExpect(jsonPath("$.data[0].completed").value(0));
        mockMvc.perform(patch("/api/shipments/" + s1 + "/deliver").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deliveredQty\":9}")).andExpect(status().isUnprocessableEntity());
        mockMvc.perform(patch("/api/shipments/" + s1 + "/deliver").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deliveredQty\":10}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("DELIVERED"));
        mockMvc.perform(patch("/api/shipments/" + s2 + "/fail").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"detail\":\"부재\"}")).andExpect(status().isBadRequest());
        mockMvc.perform(patch("/api/shipments/" + s2 + "/fail").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"CUSTOMER_ABSENT\",\"detail\":\"부재\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("FAILED"));
        mockMvc.perform(patch("/api/shipments/" + s2 + "/deliver").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deliveredQty\":2}")).andExpect(status().isConflict());

        mockMvc.perform(get("/api/orders/" + deliveredOrder))
                .andExpect(jsonPath("$.data.status").value("DELIVERED"))
                .andExpect(jsonPath("$.data.items[0].deliveredQty").value(10))
                .andExpect(jsonPath("$.data.delivery.driverName").value("홍길동"))
                .andExpect(jsonPath("$.data.timeline[6].done").value(true));
        mockMvc.perform(get("/api/orders/" + failedOrder))
                .andExpect(jsonPath("$.data.status").value("FAILED"))
                .andExpect(jsonPath("$.data.timeline[6].done").value(false));
    }
}
