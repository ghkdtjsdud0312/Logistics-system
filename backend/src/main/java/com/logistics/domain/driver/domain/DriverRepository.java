package com.logistics.domain.driver.domain;

import java.util.List;
import java.util.Optional;

public interface DriverRepository {

    Driver save(Driver driver);

    Optional<Driver> findById(Long id);

    void delete(Driver driver);

    List<Driver> findAll();

    boolean existsByDriverCode(String driverCode);

    List<Driver> findAllByStatus(DriverStatus status);

    /** 배차 확정 트랜잭션 동안 동시 확정을 막기 위한 명시적(비관적) 잠금 조회 */
    Optional<Driver> findByIdForUpdate(Long id);
}
