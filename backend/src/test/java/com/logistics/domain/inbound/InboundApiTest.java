package com.logistics.domain.inbound;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InboundApiTest {

    @Autowired
    private MockMvc mockMvc;

    /** 생성 요청을 보내고 응답의 data 값(id)을 반환한다. */
    private String create(String path, String body) throws Exception {
        String json = mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Object data = JsonPath.read(json, "$.data");
        return data instanceof Map<?, ?> map ? String.valueOf(map.get("id")) : String.valueOf(data);
    }

    @Test
    @DisplayName("입고 등록 → 입고완료 → 적치 후 재고 현황 API에 가용 100이 보인다")
    void inbound_to_stock() throws Exception {
        create("/api/products", "{\"code\":\"WATER001\",\"name\":\"생수\",\"unit\":\"EA\",\"unitWeightKg\":0.5}");
        String productId = create("/api/products", "{\"code\":\"WATER002\",\"name\":\"탄산수\",\"unit\":\"EA\",\"unitWeightKg\":0.5}");
        String warehouseId = create("/api/warehouses", "{\"code\":\"A\",\"name\":\"A창고\"}");
        String zoneId = create("/api/warehouses/" + warehouseId + "/zones", "{\"code\":\"A01\",\"name\":\"A구역\"}");
        String locationId = create("/api/zones/" + zoneId + "/locations", "{\"code\":\"A-01-01\"}");
        String inboundId = create("/api/inbounds", "{\"partnerName\":\"거래처\",\"productId\":" + productId
                + ",\"quantity\":100,\"inboundDate\":\"2026-09-25\"}");

        mockMvc.perform(patch("/api/inbounds/" + inboundId + "/receive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PUTAWAY_WAITING"));
        mockMvc.perform(patch("/api/inbounds/" + inboundId + "/putaway").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"locationId\":" + locationId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PUTAWAY_DONE"));
        mockMvc.perform(get("/api/stocks").param("stockStatus", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].onHand").value(100))
                .andExpect(jsonPath("$.data[0].reserved").value(0))
                .andExpect(jsonPath("$.data[0].available").value(100));
        mockMvc.perform(patch("/api/inbounds/" + inboundId + "/receive")).andExpect(status().isConflict());
    }
}
