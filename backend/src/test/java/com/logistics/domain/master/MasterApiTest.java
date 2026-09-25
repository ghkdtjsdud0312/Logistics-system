package com.logistics.domain.master;

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
class MasterApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("인증 없이 상품을 등록하고 조회할 수 있고 중량이 0이면 400을 반환한다")
    void product_api() throws Exception {
        String body = "{\"code\":\"RAMEN001\",\"name\":\"라면\",\"unit\":\"EA\",\"unitWeightKg\":0.12}";
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code").value("RAMEN001"));
        mockMvc.perform(get("/api/products").param("keyword", "라면"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("라면"));

        String invalid = "{\"code\":\"X\",\"name\":\"x\",\"unit\":\"EA\",\"unitWeightKg\":0}";
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("중복 상품코드는 409를 반환한다")
    void product_duplicate() throws Exception {
        String body = "{\"code\":\"DUP001\",\"name\":\"중복\",\"unit\":\"EA\",\"unitWeightKg\":1}";
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }
}
