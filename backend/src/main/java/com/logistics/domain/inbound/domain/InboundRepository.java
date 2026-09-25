package com.logistics.domain.inbound.domain;

import java.util.List;
import java.util.Optional;

public interface InboundRepository {

    Inbound save(Inbound inbound);

    Optional<Inbound> findById(Long id);

    /** status가 null이면 전체, 최신순 */
    List<Inbound> findAllByStatus(InboundStatus status);
}
