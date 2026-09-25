package com.logistics.domain.dispatch.domain;

import java.util.List;
import java.util.Optional;

public interface DispatchRepository {

    Dispatch save(Dispatch dispatch);

    Optional<Dispatch> findById(Long id);

    /** status가 null이면 전체, 최신순 */
    List<Dispatch> findAllByStatus(DispatchStatus status);

    /** 배차완료(REGISTERED) 또는 배송중(IN_TRANSIT)인 배차에 이미 배정되어 있는지 */
    boolean existsActiveByVehicleId(Long vehicleId);

    boolean existsActiveByDriverId(Long driverId);
}
