package com.logistics.domain.master;

import com.logistics.domain.inventory.application.StockService;
import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.application.WarehouseEditService;
import com.logistics.domain.master.application.WarehouseService;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.master.domain.Warehouse;
import com.logistics.domain.master.domain.Zone;
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
class MasterEditDeleteTest {

    @Autowired private ProductService productService;
    @Autowired private WarehouseService warehouseService;
    @Autowired private WarehouseEditService editService;
    @Autowired private StockService stockService;

    private Product water() {
        return productService.create(Product.builder().code("W1").name("생수").unit("EA").unitWeightKg(0.5).build());
    }

    @Test
    @DisplayName("상품을 수정하고, 사용 중이 아니면 삭제할 수 있다")
    void product_update_delete() {
        Product p = water();
        productService.update(p.getId(), "탄산수", "BOX", 1.0);
        assertThat(productService.getProduct(p.getId()).getName()).isEqualTo("탄산수");

        productService.delete(p.getId());
        assertThat(productService.search("W1")).isEmpty();
    }

    @Test
    @DisplayName("재고가 있는 상품과 위치는 삭제할 수 없다")
    void delete_blockedWhenInUse() {
        Product p = water();
        Warehouse w = warehouseService.createWarehouse("WH1", "본창고");
        Zone z = warehouseService.addZone(w.getId(), "A", "A구역");
        Long locId = warehouseService.addLocation(z.getId(), "A-01").getId();
        stockService.increase(p.getId(), locId, 5);

        assertThatThrownBy(() -> productService.delete(p.getId())).isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> editService.deleteLocation(locId)).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("하위 항목이 있는 창고·구역은 삭제할 수 없고, 비우면 아래부터 지울 수 있다")
    void warehouse_delete_order() {
        Warehouse w = warehouseService.createWarehouse("WH2", "보조창고");
        Zone z = warehouseService.addZone(w.getId(), "B", "B구역");
        Long locId = warehouseService.addLocation(z.getId(), "B-01").getId();

        assertThatThrownBy(() -> editService.deleteWarehouse(w.getId())).isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> editService.deleteZone(z.getId())).isInstanceOf(BusinessException.class);

        editService.deleteLocation(locId);
        editService.deleteZone(z.getId());
        editService.deleteWarehouse(w.getId());
        assertThat(warehouseService.getTree()).noneMatch(x -> x.getCode().equals("WH2"));
    }
}
