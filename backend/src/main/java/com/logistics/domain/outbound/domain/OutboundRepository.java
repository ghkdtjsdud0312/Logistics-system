package com.logistics.domain.outbound.domain;

import java.util.List;
import java.util.Optional;

public interface OutboundRepository {

    Outbound save(Outbound outbound);

    Optional<Outbound> findById(Long id);

    List<Outbound> findAll();

    /** 특정 inboundId를 참조하면서 주어진 상태가 아닌 출고 목록 (가용 수량 계산용) */
    List<Outbound> findByItemsInboundIdAndStatusNot(Long inboundId, OutboundStatus excludedStatus);
}
