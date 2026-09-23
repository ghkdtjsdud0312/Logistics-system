package com.logistics.domain.dispatch.application;

import com.logistics.domain.anomaly.application.AnomalyService;
import com.logistics.domain.anomaly.domain.AnomalyType;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.RouteStop;
import com.logistics.domain.dispatch.domain.RouteStopStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 배차 유스케이스 - 경유지 상태 변경 (TASK-008)
 */
@Service
@RequiredArgsConstructor
public class RouteStopService {

    private final DispatchService dispatchService;
    private final AnomalyService anomalyService;

    @Transactional
    @CacheEvict(cacheNames = "dispatchDetail", key = "#dispatchId")
    public RouteStop changeStopStatus(Long dispatchId, Long stopId, RouteStopStatus next) {
        Dispatch dispatch = dispatchService.getDispatch(dispatchId);
        RouteStop stop = dispatch.findStop(stopId);
        try {
            stop.changeStatus(next);
        } catch (BusinessException e) {
            if (e.getErrorCode() == ErrorCode.ROUTE_STOP_INVALID_TRANSITION) {
                anomalyService.record(AnomalyType.INVALID_TRANSITION, dispatchId, dispatchId + ":" + stopId,
                        "경유지 #" + stopId + " 상태 전이가 거부되었습니다.");
            }
            throw e;
        }
        return stop;
    }
}
