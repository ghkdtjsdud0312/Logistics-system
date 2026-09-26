package com.logistics.domain.master.infrastructure;

import com.logistics.domain.master.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WarehouseJpaRepository extends JpaRepository<Warehouse, Long> {

    boolean existsByCode(String code);

    @Query("SELECT z.warehouse FROM Zone z WHERE z.id = :zoneId")
    Optional<Warehouse> findByZoneId(@Param("zoneId") Long zoneId);

    @Query("SELECT l.zone.warehouse FROM Location l WHERE l.id = :locationId")
    Optional<Warehouse> findByLocationId(@Param("locationId") Long locationId);

    @Query("SELECT COUNT(l) > 0 FROM Location l WHERE l.code = :code")
    boolean existsLocationCode(@Param("code") String code);
}
