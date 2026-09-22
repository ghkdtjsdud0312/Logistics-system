package com.logistics.domain.outbound.domain;

import java.util.List;
import java.util.Optional;

public interface OutboundRepository {

    Outbound save(Outbound outbound);

    Optional<Outbound> findById(Long id);

    List<Outbound> findAll();
}
