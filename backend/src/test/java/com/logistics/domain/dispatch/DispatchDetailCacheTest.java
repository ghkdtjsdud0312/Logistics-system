package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.application.DispatchConfirmService;
import com.logistics.domain.dispatch.application.DispatchDetailService;
import com.logistics.domain.dispatch.application.DispatchStatusService;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.presentation.dto.DispatchConfirmRequest;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.vehicle.domain.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DispatchDetailCacheTest {

    @Autowired
    private DispatchConfirmService dispatchConfirmService;
    @Autowired
    private DispatchStatusService dispatchStatusService;
    @Autowired
    private DispatchDetailService dispatchDetailService;
    @Autowired
    private DispatchTestFixtures fixtures;
    @Autowired
    private CacheManager cacheManager;

    private Dispatch confirmDispatch() {
        Long outboundId = fixtures.createOutbound("서울", 10, 0.1);
        Vehicle vehicle = fixtures.createVehicle("00하0301", 500, 3.0, 10);
        Driver driver = fixtures.createDriver("캐시기사");
        return dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle.getId(), driver.getId(), List.of(outboundId), LocalDateTime.now().plusHours(1)));
    }

    @Test
    @DisplayName("배차 상세를 조회하면 캐시에 채워진다")
    void getDetail_populatesCache() {
        Dispatch dispatch = confirmDispatch();
        Cache cache = cacheManager.getCache("dispatchDetail");

        assertThat(cache.get(dispatch.getId())).isNull();
        dispatchDetailService.getDetail(dispatch.getId());
        assertThat(cache.get(dispatch.getId())).isNotNull();
    }

    @Test
    @DisplayName("배차 상태가 바뀌면 캐시가 무효화된다")
    void changeStatus_evictsCache() {
        Dispatch dispatch = confirmDispatch();
        Cache cache = cacheManager.getCache("dispatchDetail");
        dispatchDetailService.getDetail(dispatch.getId());
        assertThat(cache.get(dispatch.getId())).isNotNull();

        dispatchStatusService.changeStatus(dispatch.getId(), DispatchStatus.LOADED, dispatch.getVersion(), null, null);

        assertThat(cache.get(dispatch.getId())).isNull();
    }
}
