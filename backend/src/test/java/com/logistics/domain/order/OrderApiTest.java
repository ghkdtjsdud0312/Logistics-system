package com.logistics.domain.order;

import com.logistics.support.TestData;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TestData testData;

    private Long productId;

    @BeforeEach
    void setUp() {
        productId = testData.product("API001", 0.5);
        testData.stock(productId, testData.locations("P-01-01").get(0), 10);
    }

    private String body(int quantity) {
        return "{\"customerName\":\"김철수\",\"address\":\"서울시 강남구\",\"phone\":\"010\","
                + "\"items\":[{\"productId\":" + productId + ",\"quantity\":" + quantity + "}]}";
    }

    @Test
    @DisplayName("주문 생성은 201과 주문번호를 반환하고 목록·상세로 조회된다")
    void create_list_detail() throws Exception {
        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body(10)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("RECEIVED"))
                .andExpect(jsonPath("$.data.orderNo").isNotEmpty());
        mockMvc.perform(get("/api/orders").param("customerName", "김철수"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].quantity").value(10))
                .andExpect(jsonPath("$.data[0].status").value("RECEIVED"));
        mockMvc.perform(get("/api/stocks"))
                .andExpect(jsonPath("$.data[0].reserved").value(10))
                .andExpect(jsonPath("$.data[0].available").value(0));
    }

    @Test
    @DisplayName("가용재고 초과 주문은 409, 검증 실패는 400, 없는 주문은 404를 반환한다")
    void errors() throws Exception {
        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body(11)))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"\",\"address\":\"a\",\"phone\":\"1\",\"items\":[]}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/orders/999999")).andExpect(status().isNotFound());
    }
}
