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

    private final DispatchJpaRepository dispatchJpaRepository;

    @Override
    public Dispatch save(Dispatch dispatch) {
        // saveAndFlush: outboundIds unique 제약 위반을 이 시점에 즉시 감지하기 위해 flush를 강제한다.
        return dispatchJpaRepository.saveAndFlush(dispatch);
    }

    @Override
    public Optional<Dispatch> findById(Long id) {
        return dispatchJpaRepository.findById(id);
    }

    @Override
    public List<Dispatch> findAll() {
        return dispatchJpaRepository.findAll();
    }

    @Override
    public List<Dispatch> findActiveByVehicleId(Long vehicleId) {
        return dispatchJpaRepository.findByVehicleIdAndStatusNot(vehicleId, DispatchStatus.COMPLETED);
    }

    @Override
    public List<Dispatch> findActiveByDriverId(Long driverId) {
        return dispatchJpaRepository.findByDriverIdAndStatusNot(driverId, DispatchStatus.COMPLETED);
    }

    @Override
    public boolean existsByOutboundIdsContaining(Long outboundId) {
        return dispatchJpaRepository.existsByOutboundIdsContaining(outboundId);
    }

    @Override
    public void flush() {
        dispatchJpaRepository.flush();
    }
}
