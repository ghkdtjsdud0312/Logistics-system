package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DispatchJpaRepository extends JpaRepository<Dispatch, Long> {

    /** outboundIds를 함께 조회 - 컨트롤러에서 DTO로 변환할 때 세션이 끝나 LazyInitializationException이 나는 것을 막는다 */
    @Query("SELECT DISTINCT d FROM Dispatch d LEFT JOIN FETCH d.outboundIds")
    List<Dispatch> findAll();

    @Query("SELECT d FROM Dispatch d LEFT JOIN FETCH d.outboundIds WHERE d.id = :id")
    Optional<Dispatch> findById(@Param("id") Long id);

    List<Dispatch> findByVehicleIdAndStatusNot(Long vehicleId, DispatchStatus excludedStatus);

    List<Dispatch> findByDriverIdAndStatusNot(Long driverId, DispatchStatus excludedStatus);

    boolean existsByOutboundIdsContaining(Long outboundId);

    List<Dispatch> findByStatusAndUpdatedAtBefore(DispatchStatus status, LocalDateTime cutoff);
}
