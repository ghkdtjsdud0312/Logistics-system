package com.logistics.domain.warehouse;

import com.jayway.jsonpath.JsonPath;
import com.logistics.support.TestData;
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
class WarehouseWorkApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TestData testData;

    private String id(String json, String path) {
        return String.valueOf((Object) JsonPath.read(json, path));
    }

    @Test
    @DisplayName("주문 → 출고 지시 → 피킹 → 포장 API 흐름이 동작하고 잘못된 수량은 422를 반환한다")
    void api_flow() throws Exception {
        Long productId = testData.product("FLOW001", 0.5);
        testData.stock(productId, testData.locations("F-01-01").get(0), 10);
        String created = mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"김철수\",\"address\":\"서울\",\"phone\":\"010\","
                                + "\"items\":[{\"productId\":" + productId + ",\"quantity\":10}]}"))
                .andReturn().getResponse().getContentAsString();
        String orderId = id(created, "$.data.id");

        mockMvc.perform(post("/api/orders/" + orderId + "/release"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("OUTBOUND_WAITING"))
                .andExpect(jsonPath("$.data.pickingTaskCount").value(1));
        String list = mockMvc.perform(get("/api/picking-tasks").param("status", "WAITING"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].locationCode").value("F-01-01"))
                .andReturn().getResponse().getContentAsString();
        String taskId = id(list, "$.data[0].id");

        mockMvc.perform(patch("/api/picking-tasks/" + taskId + "/start")).andExpect(status().isOk());
        mockMvc.perform(patch("/api/picking-tasks/" + taskId + "/complete").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pickedQty\":9}")).andExpect(status().isUnprocessableEntity());
        mockMvc.perform(patch("/api/picking-tasks/" + taskId + "/complete").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pickedQty\":10}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        String packing = mockMvc.perform(get("/api/packing-tasks").param("status", "WAITING"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        mockMvc.perform(patch("/api/packing-tasks/" + id(packing, "$.data[0].id") + "/complete")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"boxCode\":\"BOX-001\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(jsonPath("$.data.status").value("PACKED"))
                .andExpect(jsonPath("$.data.items[0].pickedQty").value(10));
    }
}
