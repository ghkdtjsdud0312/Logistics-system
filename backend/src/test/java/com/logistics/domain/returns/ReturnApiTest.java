package com.logistics.domain.returns;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReturnApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TestData testData;
    @Autowired private TestFlow testFlow;

    @Test
    @DisplayName("배송 실패 API 후 반품 목록에 나타나고 회수 → 반품입고 → 처리완료 API가 동작한다")
    void api_flow() throws Exception {
        Long shipmentId = testFlow.deliveringShipment(3);
        mockMvc.perform(patch("/api/shipments/" + shipmentId + "/fail").contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\":\"CUSTOMER_ABSENT\",\"detail\":\"부재\"}")).andExpect(status().isOk());

        String list = mockMvc.perform(get("/api/returns").param("status", "REQUESTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].reason").value("CUSTOMER_ABSENT"))
                .andExpect(jsonPath("$.data[0].quantity").value(3))
                .andReturn().getResponse().getContentAsString();
        String id = String.valueOf((Object) JsonPath.read(list, "$.data[0].id"));
        Long locationId = testData.locations("API-R-01").get(0);

        mockMvc.perform(patch("/api/returns/" + id + "/receive")).andExpect(status().isConflict());
        mockMvc.perform(patch("/api/returns/" + id + "/collect")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COLLECTING"));
        mockMvc.perform(patch("/api/returns/" + id + "/collected")).andExpect(status().isOk());
        mockMvc.perform(patch("/api/returns/" + id + "/receive")).andExpect(status().isUnprocessableEntity());
        mockMvc.perform(patch("/api/returns/" + id + "/receive").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"locationId\":" + locationId + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("RETURN_RECEIVED"));
        mockMvc.perform(patch("/api/returns/" + id + "/complete")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
        mockMvc.perform(get("/api/stocks").param("keyword", "P"))
                .andExpect(status().isOk());
    }
}
