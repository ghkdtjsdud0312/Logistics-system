package com.logistics.domain.master;

import com.logistics.domain.master.application.WarehouseEditService;
import com.logistics.domain.master.application.WarehouseService;
import com.logistics.domain.master.domain.Warehouse;
import com.logistics.domain.order.application.CreateOrderCommand;
import com.logistics.domain.order.application.CreateOrderCommand.Line;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.Order;
import com.logistics.support.FakeGeocodingClient;
import com.logistics.support.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class GeocodingTest {

    @Autowired private WarehouseService warehouseService;
    @Autowired private WarehouseEditService editService;
    @Autowired private OrderService orderService;
    @Autowired private FakeGeocodingClient geocoder;
    @Autowired private TestData testData;

    @AfterEach
    void cleanUp() {
        geocoder.clear();
    }

    @Test
    @DisplayName("창고 주소를 좌표로 변환해 저장하고, 변환에 실패해도 창고는 생성되며 주소를 바꾸면 다시 변환한다")
    void warehouse() {
        geocoder.register("서울 본사", 37.5, 127.0);
        Warehouse located = warehouseService.createWarehouse("G1", "본사창고", "서울 본사");
        assertThat(located.getLatitude()).isEqualTo(37.5);
        assertThat(located.getLongitude()).isEqualTo(127.0);

        Warehouse unknown = warehouseService.createWarehouse("G2", "미확인창고", "없는 주소");
        assertThat(unknown.getAddress()).isEqualTo("없는 주소");
        assertThat(unknown.getLatitude()).isNull();

        geocoder.register("부산 지사", 35.1, 129.0);
        editService.updateWarehouse(unknown.getId(), "미확인창고", "부산 지사");
        assertThat(unknown.getLatitude()).isEqualTo(35.1);
    }

    @Test
    @DisplayName("주문을 만들면 주소가 좌표로 변환되고 변환에 실패하면 좌표 없이 주문이 만들어진다")
    void order() {
        long seed = System.nanoTime();
        Long productId = testData.product("GEO" + seed, 0.5);
        testData.stock(productId, testData.locations("GL" + seed).get(0), 2);
        geocoder.register("강원도 삼척시", 37.45, 129.16);
        Order located = create("강원도 삼척시", productId);
        assertThat(located.hasCoordinate()).isTrue();
        assertThat(located.getLatitude()).isEqualTo(37.45);

        Order unlocated = create("알 수 없는 곳", productId);
        assertThat(unlocated.hasCoordinate()).isFalse();
        assertThat(unlocated.getOrderNo()).isNotNull();
    }

    private Order create(String address, Long productId) {
        return orderService.create(new CreateOrderCommand("홍길동", address, "010-0000-0000",
                List.of(new Line(productId, 1))));
    }
}
