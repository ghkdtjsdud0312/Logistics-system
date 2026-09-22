package com.logistics.domain.dispatch.domain;

import java.util.List;
import java.util.Optional;

public interface DispatchRepository {

    Dispatch save(Dispatch dispatch);

    Optional<Dispatch> findById(Long id);

    List<Dispatch> findAll();
}
