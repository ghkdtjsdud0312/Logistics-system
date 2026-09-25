package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DispatchJpaRepository extends JpaRepository<Dispatch, Long> {

    List<Dispatch> findAllByOrderByIdDesc();

    List<Dispatch> findAllByStatusOrderByIdDesc(DispatchStatus status);

    boolean existsByVehicleIdAndStatusIn(Long vehicleId, Collection<DispatchStatus> statuses);

    boolean existsByDriverIdAndStatusIn(Long driverId, Collection<DispatchStatus> statuses);
}
