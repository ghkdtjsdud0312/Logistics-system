package com.logistics.domain.vehicle.infrastructure;

import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.domain.vehicle.domain.VehicleRepository;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VehicleRepositoryImpl implements VehicleRepository {

    private final VehicleJpaRepository vehicleJpaRepository;

    @Override
    public Vehicle save(Vehicle vehicle) {
        return vehicleJpaRepository.save(vehicle);
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return vehicleJpaRepository.findById(id);
    }

    @Override
    public void delete(Vehicle vehicle) {
        vehicleJpaRepository.delete(vehicle);
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleJpaRepository.findAll();
    }

    @Override
    public boolean existsByVehicleNumber(String vehicleNumber) {
        return vehicleJpaRepository.existsByVehicleNumber(vehicleNumber);
    }

    @Override
    public List<Vehicle> findAllByStatus(VehicleStatus status) {
        return vehicleJpaRepository.findAllByStatus(status);
    }

    @Override
    public Optional<Vehicle> findByIdForUpdate(Long id) {
        return vehicleJpaRepository.findByIdForUpdate(id);
    }
}
