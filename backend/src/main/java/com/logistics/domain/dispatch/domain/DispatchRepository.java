package com.logistics.domain.dispatch.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DispatchRepository {

    Dispatch save(Dispatch dispatch);

    Optional<Dispatch> findById(Long id);

    List<Dispatch> findAll();

    /** 완료되지 않은(COMPLETED가 아닌) 배차만 조회 - 일정 겹침 판단용 */
    List<Dispatch> findActiveByVehicleId(Long vehicleId);

    List<Dispatch> findActiveByDriverId(Long driverId);

    /** IN_TRANSIT 상태가 기준 시각 이전부터 유지된 배차 - 정체 탐지용 */
    List<Dispatch> findStalledInTransit(LocalDateTime cutoff);

    boolean existsByOutboundIdsContaining(Long outboundId);

    /** 이미 관리 중인(managed) 엔티티의 변경을 즉시 DB에 반영한다 (예: IDENTITY 자식의 id를 바로 사용해야 할 때). */
    void flush();
}
