package com.logistics.domain.master.application;

import com.logistics.domain.master.domain.Location;
import com.logistics.domain.master.domain.Warehouse;
import com.logistics.domain.master.domain.WarehouseRepository;
import com.logistics.domain.master.domain.Zone;
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

    public List<Warehouse> getTree() {
        return warehouseRepository.findAll();
    }

    @Transactional
    public Warehouse createWarehouse(String code, String name) {
        if (warehouseRepository.existsByCode(code)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CODE);
        }
        return warehouseRepository.save(new Warehouse(code, name));
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
