package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.DispatchStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispatchStatusHistoryJpaRepository extends JpaRepository<DispatchStatusHistory, Long> {

    List<DispatchStatusHistory> findAllByDispatchIdOrderByCreatedAtAsc(Long dispatchId);
}
