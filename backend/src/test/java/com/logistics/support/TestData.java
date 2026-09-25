package com.logistics.support;

import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.inventory.application.StockService;
import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.application.WarehouseService;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.master.domain.Warehouse;
import com.logistics.domain.vehicle.application.VehicleService;
import com.logistics.domain.vehicle.domain.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/** 테스트용 기준정보·재고 준비와 (트랜잭션 없는 테스트를 위한) 데이터 정리 */
@Component
@RequiredArgsConstructor
public class TestData {

    private static final List<String> TABLES = List.of("stock_reservation", "picking_task", "packing_task", "shipment",
            "dispatch", "order_item", "orders", "stock", "inbound", "product", "location", "zone", "warehouse",
            "vehicle", "driver");

    private final ProductService productService;
    private final WarehouseService warehouseService;
    private final StockService stockService;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final JdbcTemplate jdbcTemplate;

    public Long product(String code, double unitWeightKg) {
        return productService.create(Product.builder()
                .code(code).name(code + " 상품").unit("EA").unitWeightKg(unitWeightKg).build()).getId();
    }

    /** 창고 1개, 구역 1개, 요청한 코드의 위치들을 만들고 위치 ID 목록을 반환한다. */
    public List<Long> locations(String... locationCodes) {
        Warehouse warehouse = warehouseService.createWarehouse("W" + System.nanoTime(), "테스트창고");
        Long zoneId = warehouseService.addZone(warehouse.getId(), "Z01", "테스트구역").getId();
        return java.util.Arrays.stream(locationCodes)
                .map(code -> warehouseService.addLocation(zoneId, code).getId()).toList();
    }

    public Long vehicle(double capacityKg) {
        return vehicleService.create(Vehicle.builder()
                .vehicleNumber("V" + System.nanoTime()).vehicleType("1톤").capacityKg(capacityKg).build()).getId();
    }

    public Long driver() {
        return driverService.create(Driver.builder()
                .driverCode("D" + System.nanoTime()).name("홍길동").phone("010").build()).getId();
    }

    public void stock(Long productId, Long locationId, int quantity) {
        stockService.increase(productId, locationId, quantity);
    }

    public void clear() {
        TABLES.forEach(table -> jdbcTemplate.execute("DELETE FROM " + table));
    }
}
