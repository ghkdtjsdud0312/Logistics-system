package com.logistics.domain.outbound.infrastructure;

import com.logistics.domain.outbound.domain.Outbound;
import com.logistics.domain.outbound.domain.OutboundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OutboundJpaRepository extends JpaRepository<Outbound, Long> {

    /** items를 함께 조회 - 컨트롤러에서 DTO로 변환할 때 세션이 끝나 LazyInitializationException이 나는 것을 막는다 */
    @Query("SELECT DISTINCT o FROM Outbound o LEFT JOIN FETCH o.items")
    List<Outbound> findAll();

    @Query("SELECT o FROM Outbound o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Outbound> findById(@Param("id") Long id);

    List<Outbound> findByItems_InboundIdAndStatusNot(Long inboundId, OutboundStatus excludedStatus);
}
