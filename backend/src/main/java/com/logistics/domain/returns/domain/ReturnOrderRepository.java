package com.logistics.domain.returns.domain;

import java.util.List;
import java.util.Optional;

public interface ReturnOrderRepository {

    ReturnOrder save(ReturnOrder returnOrder);

    Optional<ReturnOrder> findById(Long id);

    /** status가 null이면 전체, 최신순 */
    List<ReturnOrder> findAllByStatus(ReturnStatus status);
}
