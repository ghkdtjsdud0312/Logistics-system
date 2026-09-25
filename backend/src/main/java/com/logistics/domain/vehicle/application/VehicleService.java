package com.logistics.domain.vehicle.application;

import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.domain.vehicle.domain.VehicleRepository;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Vehicle getVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));
    }

    public List<Vehicle> getVehicles(VehicleStatus status) {
        return status == null ? vehicleRepository.findAll() : vehicleRepository.findAllByStatus(status);
    }

    /** 호출자의 트랜잭션이 끝날 때까지 해당 차량 행을 잠근다 (배차 확정 동시성 보호용) */
    @Transactional
    public Vehicle getVehicleForUpdate(Long id) {
        return vehicleRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));
    }

    @Transactional
    public Vehicle create(Vehicle vehicle) {
        if (vehicleRepository.existsByVehicleNumber(vehicle.getVehicleNumber())) {
            throw new BusinessException(ErrorCode.DUPLICATE_CODE);
        }
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle changeStatus(Long id, VehicleStatus status) {
        Vehicle vehicle = getVehicle(id);
        vehicle.changeStatus(status);
        return vehicle;
    }
}
