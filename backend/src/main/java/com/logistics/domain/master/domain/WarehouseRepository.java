package com.logistics.domain.master.domain;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository {

    Warehouse save(Warehouse warehouse);

    Optional<Warehouse> findById(Long id);

    Optional<Warehouse> findByZoneId(Long zoneId);

    List<Warehouse> findAll();

    boolean existsByCode(String code);

    boolean existsLocationCode(String locationCode);
}
