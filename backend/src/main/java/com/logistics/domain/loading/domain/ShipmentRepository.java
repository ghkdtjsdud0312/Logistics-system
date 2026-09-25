package com.logistics.domain.loading.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ShipmentRepository {

    Shipment save(Shipment shipment);

    Optional<Shipment> findById(Long id);

    /** 배차 등록 시 동시 배정을 막기 위해 행을 잠그고 조회한다. */
    List<Shipment> findAllByIdForUpdate(Collection<Long> ids);

    List<Shipment> findAllByDispatchId(Long dispatchId);

    /** status, dispatchId가 null이면 해당 조건 무시, 최신순 */
    List<Shipment> search(ShipmentStatus status, Long dispatchId);
}
