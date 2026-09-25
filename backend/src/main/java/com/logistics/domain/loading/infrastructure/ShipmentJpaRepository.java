package com.logistics.domain.loading.infrastructure;

import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ShipmentJpaRepository extends JpaRepository<Shipment, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Shipment s WHERE s.id IN :ids ORDER BY s.id")
    List<Shipment> findAllByIdForUpdate(@Param("ids") Collection<Long> ids);

    List<Shipment> findAllByDispatchIdOrderById(Long dispatchId);

    List<Shipment> findAllByOrderByIdDesc();

    List<Shipment> findAllByStatusOrderByIdDesc(ShipmentStatus status);

    List<Shipment> findAllByDispatchIdOrderByIdDesc(Long dispatchId);

    List<Shipment> findAllByStatusAndDispatchIdOrderByIdDesc(ShipmentStatus status, Long dispatchId);
}
