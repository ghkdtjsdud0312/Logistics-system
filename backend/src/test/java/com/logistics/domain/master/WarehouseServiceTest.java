package com.logistics.domain.master;

import com.logistics.domain.master.application.WarehouseService;
import com.logistics.domain.master.domain.Warehouse;
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
class WarehouseServiceTest {

    @Autowired
    private WarehouseService warehouseService;

    @Test
    @DisplayName("창고 → 구역 → 위치를 등록하면 트리로 조회된다")
    void tree() {
        Warehouse warehouse = warehouseService.createWarehouse("A", "A창고");
        Long zoneId = warehouseService.addZone(warehouse.getId(), "A01", "A구역").getId();
        warehouseService.addLocation(zoneId, "A-01-01");
        warehouseService.addLocation(zoneId, "A-01-02");

        Warehouse found = warehouseService.getTree().get(0);

        assertThat(found.getZones()).hasSize(1);
        assertThat(found.getZones().get(0).getLocations()).extracting("code")
                .containsExactly("A-01-01", "A-01-02");
    }

    @Test
    @DisplayName("위치 코드는 창고 전체에서 중복될 수 없다")
    void location_duplicateCode() {
        Warehouse a = warehouseService.createWarehouse("A", "A창고");
        Warehouse b = warehouseService.createWarehouse("B", "B창고");
        Long zoneA = warehouseService.addZone(a.getId(), "A01", "A구역").getId();
        Long zoneB = warehouseService.addZone(b.getId(), "B01", "B구역").getId();
        warehouseService.addLocation(zoneA, "X-01-01");

        assertThatThrownBy(() -> warehouseService.addLocation(zoneB, "X-01-01"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("같은 창고 안에서 구역 코드는 중복될 수 없다")
    void zone_duplicateCode() {
        Warehouse a = warehouseService.createWarehouse("A", "A창고");
        warehouseService.addZone(a.getId(), "A01", "A구역");

        assertThatThrownBy(() -> warehouseService.addZone(a.getId(), "A01", "다른 구역"))
                .isInstanceOf(BusinessException.class);
    }
}
