package com.logistics.domain.loading.infrastructure;

import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentRepository;
import com.logistics.domain.loading.domain.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ShipmentRepositoryImpl implements ShipmentRepository {

    private final ShipmentJpaRepository jpaRepository;

    @Override
    public Shipment save(Shipment shipment) {
        return jpaRepository.saveAndFlush(shipment);
    }

    @Override
    public Optional<Shipment> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Shipment> findAllByIdForUpdate(Collection<Long> ids) {
        return jpaRepository.findAllByIdForUpdate(ids);
    }

    @Override
    public List<Shipment> findAllByDispatchId(Long dispatchId) {
        return jpaRepository.findAllByDispatchIdOrderById(dispatchId);
    }

    @Override
    public List<Shipment> search(ShipmentStatus status, Long dispatchId) {
        if (status != null && dispatchId != null) {
            return jpaRepository.findAllByStatusAndDispatchIdOrderByIdDesc(status, dispatchId);
        }
        if (status != null) {
            return jpaRepository.findAllByStatusOrderByIdDesc(status);
        }
        return dispatchId != null ? jpaRepository.findAllByDispatchIdOrderByIdDesc(dispatchId)
                : jpaRepository.findAllByOrderByIdDesc();
    }
}
