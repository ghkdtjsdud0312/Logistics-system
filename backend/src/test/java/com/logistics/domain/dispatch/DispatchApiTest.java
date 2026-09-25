package com.logistics.domain.dispatch;

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
class DispatchApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TestData testData;
    @Autowired private TestFlow testFlow;

    private String first(String json, String path) {
        return String.valueOf((Object) JsonPath.read(json, path));
    }

    @Test
    @DisplayName("상차 대기 조회 → 상차 → 배차 등록 → 배송 시작 API 흐름과 과적 422")
    void api_flow() throws Exception {
        Long orderId = testFlow.packedOrder(0.5, 10);
        mockMvc.perform(get("/api/loadings/waiting-orders"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(orderId));
        String loaded = mockMvc.perform(post("/api/loadings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderIds\":[" + orderId + "]}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data[0].totalWeightKg").value(5.0))
                .andReturn().getResponse().getContentAsString();
        String shipmentId = first(loaded, "$.data[0].id");
        mockMvc.perform(get("/api/shipments").param("status", "LOADED"))
                .andExpect(jsonPath("$.data[0].id").value(Long.parseLong(shipmentId)));

        String overload = "{\"vehicleId\":" + testData.vehicle(4) + ",\"driverId\":" + testData.driver()
                + ",\"plannedStartAt\":\"2026-09-25T13:00:00\",\"plannedArrivalAt\":\"2026-09-25T15:00:00\",\"shipmentIds\":[" + shipmentId + "]}";
        mockMvc.perform(post("/api/dispatches").contentType(MediaType.APPLICATION_JSON).content(overload))
                .andExpect(status().isUnprocessableEntity());

        String ok = overload.replaceFirst("\"vehicleId\":\\d+", "\"vehicleId\":" + testData.vehicle(1000))
                .replaceFirst("\"driverId\":\\d+", "\"driverId\":" + testData.driver());
        String created = mockMvc.perform(post("/api/dispatches").contentType(MediaType.APPLICATION_JSON).content(ok))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("REGISTERED"))
                .andExpect(jsonPath("$.data.shipmentCount").value(1)).andReturn().getResponse().getContentAsString();
        String dispatchId = first(created, "$.data.id");

        mockMvc.perform(patch("/api/dispatches/" + dispatchId + "/start"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("IN_TRANSIT"));
        mockMvc.perform(patch("/api/dispatches/" + dispatchId + "/cancel")).andExpect(status().isConflict());
        mockMvc.perform(get("/api/orders/" + orderId)).andExpect(jsonPath("$.data.status").value("IN_DELIVERY"));
    }
}
