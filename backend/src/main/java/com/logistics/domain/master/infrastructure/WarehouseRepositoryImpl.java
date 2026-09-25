package com.logistics.domain.master.infrastructure;

import com.logistics.domain.master.domain.Warehouse;
import com.logistics.domain.master.domain.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WarehouseRepositoryImpl implements WarehouseRepository {

    private final WarehouseJpaRepository jpaRepository;

    @Override
    public Warehouse save(Warehouse warehouse) {
        return jpaRepository.saveAndFlush(warehouse);
    }

    @Override
    public Optional<Warehouse> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Warehouse> findByZoneId(Long zoneId) {
        return jpaRepository.findByZoneId(zoneId);
    }

    @Override
    public List<Warehouse> findAll() {
        return jpaRepository.findAll(Sort.by("code"));
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public boolean existsLocationCode(String locationCode) {
        return jpaRepository.existsLocationCode(locationCode);
    }
}
