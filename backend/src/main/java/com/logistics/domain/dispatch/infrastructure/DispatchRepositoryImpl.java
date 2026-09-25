package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchRepository;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DispatchRepositoryImpl implements DispatchRepository {

    private static final List<DispatchStatus> ACTIVE = List.of(DispatchStatus.REGISTERED, DispatchStatus.IN_TRANSIT);

    private final DispatchJpaRepository jpaRepository;

    @Override
    public Dispatch save(Dispatch dispatch) {
        return jpaRepository.saveAndFlush(dispatch);
    }

    @Override
    public Optional<Dispatch> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Dispatch> findAllByStatus(DispatchStatus status) {
        return status == null ? jpaRepository.findAllByOrderByIdDesc()
                : jpaRepository.findAllByStatusOrderByIdDesc(status);
    }

    @Override
    public boolean existsActiveByVehicleId(Long vehicleId) {
        return jpaRepository.existsByVehicleIdAndStatusIn(vehicleId, ACTIVE);
    }

    @Override
    public boolean existsActiveByDriverId(Long driverId) {
        return jpaRepository.existsByDriverIdAndStatusIn(driverId, ACTIVE);
    }
}
