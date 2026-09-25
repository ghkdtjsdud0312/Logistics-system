package com.logistics.domain.master;

import com.logistics.support.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 트랜잭션 없이 실제 요청 경계에서 창고 트리를 조회한다.
 * (테스트 전체가 하나의 트랜잭션이면 지연 로딩 오류를 놓치므로 별도로 검증한다.)
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class WarehouseTreeApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TestData testData;

    @AfterEach
    void cleanUp() {
        testData.clear();
    }

    @Test
    @DisplayName("창고 트리 API는 트랜잭션 밖에서도 구역·위치까지 포함해 200으로 응답한다")
    void tree_outsideTransaction() throws Exception {
        testData.locations("T-01-01", "T-01-02");

        mockMvc.perform(get("/api/warehouses/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].zones[0].locations.length()").value(2))
                .andExpect(jsonPath("$.data[0].zones[0].locations[0].code").value("T-01-01"));
    }
}
