package com.logistics.domain.driver.application;

import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.driver.domain.DriverRepository;
import com.logistics.domain.driver.domain.DriverStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.reference.ReferenceGuard;
import com.logistics.global.reference.ReferenceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;
    private final ReferenceGuard referenceGuard;

    public Driver getDriver(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRIVER_NOT_FOUND));
    }

    public List<Driver> getDrivers(DriverStatus status) {
        return status == null ? driverRepository.findAll() : driverRepository.findAllByStatus(status);
    }

    /** 호출자의 트랜잭션이 끝날 때까지 해당 기사 행을 잠근다 (배차 확정 동시성 보호용) */
    @Transactional
    public Driver getDriverForUpdate(Long id) {
        return driverRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRIVER_NOT_FOUND));
    }

    @Transactional
    public Driver create(Driver driver) {
        if (driverRepository.existsByDriverCode(driver.getDriverCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_CODE);
        }
        return driverRepository.save(driver);
    }

    @Transactional
    public Driver changeStatus(Long id, DriverStatus status) {
        Driver driver = getDriver(id);
        driver.changeStatus(status);
        return driver;
    }

    @Transactional
    public Driver update(Long id, String name, String phone) {
        Driver driver = getDriver(id);
        driver.update(name, phone);
        return driver;
    }

    @Transactional
    public void delete(Long id) {
        Driver driver = getDriver(id);
        referenceGuard.assertNotReferenced(ReferenceType.DRIVER, id);
        driverRepository.delete(driver);
    }
}
