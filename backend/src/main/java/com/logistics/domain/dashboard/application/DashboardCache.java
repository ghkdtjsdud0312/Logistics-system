package com.logistics.domain.dashboard.application;

import java.util.Optional;

/** 대시보드 조회 결과 캐시(Cache-Aside). 캐시 장애는 조회를 막지 않고 빈 결과로 처리한다. */
public interface DashboardCache {

    <T> Optional<T> get(String key, Class<T> type);

    void put(String key, Object value);

    void evictAll();
}
