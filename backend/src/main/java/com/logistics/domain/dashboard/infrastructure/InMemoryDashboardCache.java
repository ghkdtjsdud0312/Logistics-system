package com.logistics.domain.dashboard.infrastructure;

import com.logistics.domain.dashboard.application.DashboardCache;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** 테스트 프로파일용 인메모리 캐시. Redis 없이 히트·무효화 동작을 검증하기 위한 대체재 (ADR-010) */
@Component
@Profile("test")
public class InMemoryDashboardCache implements DashboardCache {

    private final Map<String, Object> store = new ConcurrentHashMap<>();

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        return Optional.ofNullable(store.get(key)).map(type::cast);
    }

    @Override
    public void put(String key, Object value) {
        store.put(key, value);
    }

    @Override
    public void evictAll() {
        store.clear();
    }
}
