package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchJpaRepository extends JpaRepository<Dispatch, Long> {
}
