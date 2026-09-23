package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.presentation.dto.DispatchDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 배차 상세 조회 (Redis cache-aside, Day 5)
 * - Key: dispatchDetail::{id} (RedisConfig에서 "dispatch:detail:" 접두어로 설정)
 * - 무효화는 배차 상태/경유지 상태/경로가 바뀌는 지점(@CacheEvict)에서 수행한다.
 */
@Service
@RequiredArgsConstructor
public class DispatchDetailService {

    private final DispatchService dispatchService;
    private final DispatchStatusService dispatchStatusService;

    @Cacheable(cacheNames = "dispatchDetail", key = "#id")
    public DispatchDetailResponse getDetail(Long id) {
        var dispatch = dispatchService.getDispatchWithStops(id);
        var history = dispatchStatusService.getHistory(id);
        return DispatchDetailResponse.of(dispatch, history);
    }
}
