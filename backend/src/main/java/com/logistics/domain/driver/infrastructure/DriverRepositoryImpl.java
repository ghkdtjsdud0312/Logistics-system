package com.logistics.domain.driver.infrastructure;

import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.driver.domain.DriverRepository;
import com.logistics.domain.driver.domain.DriverStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DriverRepositoryImpl implements DriverRepository {

    private final DriverJpaRepository driverJpaRepository;

    @Override
    public Driver save(Driver driver) {
        return driverJpaRepository.save(driver);
    }

    @Override
    public Optional<Driver> findById(Long id) {
        return driverJpaRepository.findById(id);
    }

    @Override
    public void delete(Driver driver) {
        driverJpaRepository.delete(driver);
    }

    @Override
    public List<Driver> findAll() {
        return driverJpaRepository.findAll();
    }

    @Override
    public boolean existsByDriverCode(String driverCode) {
        return driverJpaRepository.existsByDriverCode(driverCode);
    }

    @Override
    public List<Driver> findAllByStatus(DriverStatus status) {
        return driverJpaRepository.findAllByStatus(status);
    }

    @Override
    public Optional<Driver> findByIdForUpdate(Long id) {
        return driverJpaRepository.findByIdForUpdate(id);
    }
}
