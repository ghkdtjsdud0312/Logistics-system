package com.logistics.domain.master.application;

import com.logistics.domain.master.domain.Location;
import com.logistics.domain.master.domain.Warehouse;
import com.logistics.domain.master.domain.WarehouseRepository;
import com.logistics.domain.master.domain.Zone;
import com.logistics.domain.master.presentation.dto.WarehouseTreeResponse;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseGeocoder warehouseGeocoder;

    public List<Warehouse> getTree() {
        return warehouseRepository.findAll();
    }

    /** 지연 로딩되는 구역·위치를 트랜잭션 안에서 DTO로 변환해 돌려준다. */
    public List<WarehouseTreeResponse> getTreeResponses() {
        return warehouseRepository.findAll().stream().map(WarehouseTreeResponse::from).toList();
    }

    public Warehouse get(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND));
    }

    /** 좌표가 없으면 주소로 한 번 더 변환을 시도한 창고를 돌려준다. */
    @Transactional
    public Warehouse getLocated(Long id) {
        Warehouse warehouse = get(id);
        if (warehouse.getLatitude() == null && warehouse.getAddress() != null) {
            warehouseGeocoder.locate(warehouse, warehouse.getAddress());
        }
        return warehouse;
    }

    @Transactional
    public Warehouse createWarehouse(String code, String name) {
        return createWarehouse(code, name, null);
    }

    @Transactional
    public Warehouse createWarehouse(String code, String name, String address) {
        if (warehouseRepository.existsByCode(code)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CODE);
        }
        Warehouse warehouse = new Warehouse(code, name);
        warehouseGeocoder.locate(warehouse, address);
        return warehouseRepository.save(warehouse);
    }

    @Transactional
    public Zone addZone(Long warehouseId, String code, String name) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND));
        warehouse.addZone(code, name);
        // save(merge)가 새 자식의 관리 인스턴스를 만들므로 저장 후 코드로 다시 조회한다.
        return warehouseRepository.save(warehouse).getZone(code);
    }

    @Transactional
    public Location addLocation(Long zoneId, String code) {
        if (warehouseRepository.existsLocationCode(code)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CODE);
        }
        Warehouse warehouse = warehouseRepository.findByZoneId(zoneId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ZONE_NOT_FOUND));
        Zone zone = warehouse.findZone(zoneId);
        zone.addLocation(code);
        warehouseRepository.save(warehouse);
        return zone.getLocation(code);
    }
}
