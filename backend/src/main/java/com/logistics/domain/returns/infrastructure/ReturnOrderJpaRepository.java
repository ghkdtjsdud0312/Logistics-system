package com.logistics.domain.returns.infrastructure;

import com.logistics.domain.returns.domain.ReturnOrder;
import com.logistics.domain.returns.domain.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnOrderJpaRepository extends JpaRepository<ReturnOrder, Long> {

    List<ReturnOrder> findAllByOrderByIdDesc();

    List<ReturnOrder> findAllByStatusOrderByIdDesc(ReturnStatus status);
}
