package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.DispatchStatusProjection;
import org.springframework.data.jpa.repository.JpaRepository;

/** 조회 전용 투영 저장소 (도메인 애그리거트가 아니므로 Repository 인터페이스 추상화 없이 직접 사용) */
public interface DispatchStatusProjectionRepository extends JpaRepository<DispatchStatusProjection, Long> {
}
