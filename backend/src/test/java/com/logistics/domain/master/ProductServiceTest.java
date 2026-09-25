package com.logistics.domain.master;

import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.domain.Product;
import com.logistics.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    private Product water() {
        return Product.builder().code("WATER001").name("생수 500ml").unit("EA").unitWeightKg(0.5).build();
    }

    @Test
    @DisplayName("상품을 등록하고 코드나 이름으로 검색할 수 있다")
    void create_andSearch() {
        productService.create(water());

        assertThat(productService.search("WATER")).hasSize(1);
        assertThat(productService.search("생수")).hasSize(1);
        assertThat(productService.search("라면")).isEmpty();
    }

    @Test
    @DisplayName("같은 상품코드는 중복 등록할 수 없다")
    void create_duplicateCode() {
        productService.create(water());

        assertThatThrownBy(() -> productService.create(water())).isInstanceOf(BusinessException.class);
    }
}
