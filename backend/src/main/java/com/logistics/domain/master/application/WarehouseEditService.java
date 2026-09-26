package com.logistics.domain.master.application;

import com.logistics.domain.master.domain.Warehouse;
import com.logistics.domain.master.domain.WarehouseRepository;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.reference.ReferenceGuard;
import com.logistics.global.reference.ReferenceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 창고·구역·위치 수정/삭제 (등록은 WarehouseService) */
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseEditService {

    private final WarehouseRepository warehouseRepository;
    private final ReferenceGuard referenceGuard;

    public Long renameWarehouse(Long id, String name) {
        Warehouse warehouse = getWarehouse(id);
        warehouse.rename(name);
        return warehouse.getId();
    }

    public void deleteWarehouse(Long id) {
        Warehouse warehouse = getWarehouse(id);
        warehouse.assertDeletable();
        warehouseRepository.delete(warehouse);
    }

    public Long renameZone(Long zoneId, String name) {
        Warehouse warehouse = getByZone(zoneId);
        warehouse.findZone(zoneId).rename(name);
        return zoneId;
    }

    public void deleteZone(Long zoneId) {
        Warehouse warehouse = getByZone(zoneId);
        warehouse.removeZone(zoneId);
        warehouseRepository.save(warehouse);
    }

    public void deleteLocation(Long locationId) {
        Warehouse warehouse = warehouseRepository.findByLocationId(locationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));
        referenceGuard.assertNotReferenced(ReferenceType.LOCATION, locationId);
        warehouse.removeLocation(locationId);
        warehouseRepository.save(warehouse);
    }

    private Warehouse getWarehouse(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND));
    }

    private Warehouse getByZone(Long zoneId) {
        return warehouseRepository.findByZoneId(zoneId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ZONE_NOT_FOUND));
    }
}
