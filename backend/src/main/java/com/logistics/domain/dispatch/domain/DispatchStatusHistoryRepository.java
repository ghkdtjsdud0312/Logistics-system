package com.logistics.domain.dispatch.domain;

import java.util.List;

public interface DispatchStatusHistoryRepository {

    DispatchStatusHistory save(DispatchStatusHistory history);

    List<DispatchStatusHistory> findAllByDispatchId(Long dispatchId);
}
