package com.logistics.domain.vehicle.domain;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {

    Vehicle save(Vehicle vehicle);

    Optional<Vehicle> findById(Long id);

    void delete(Vehicle vehicle);

    List<Vehicle> findAll();

    boolean existsByVehicleNumber(String vehicleNumber);

    List<Vehicle> findAllByStatus(VehicleStatus status);

    /** 배차 확정 트랜잭션 동안 동시 확정을 막기 위한 명시적(비관적) 잠금 조회 */
    Optional<Vehicle> findByIdForUpdate(Long id);
}
