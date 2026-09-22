package com.logistics.domain.inbound.domain;

import java.util.List;
import java.util.Optional;

/**
 * 입고 도메인 리포지토리 포트(Port)
 * - 구현체는 infrastructure 계층의 InboundRepositoryImpl 에서 JPA로 제공
 */
public interface InboundRepository {

    Inbound save(Inbound inbound);

    Optional<Inbound> findById(Long id);

    List<Inbound> findAll();
}
