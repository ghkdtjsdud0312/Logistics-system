package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.RouteStop;
import com.logistics.domain.dispatch.domain.RouteStopStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 배차 유스케이스 - 경유지 상태 변경 (TASK-008)
 */
@Service
@RequiredArgsConstructor
public class RouteStopService {

    private final DispatchService dispatchService;

    @Transactional
    public RouteStop changeStopStatus(Long dispatchId, Long stopId, RouteStopStatus next) {
        Dispatch dispatch = dispatchService.getDispatch(dispatchId);
        RouteStop stop = dispatch.findStop(stopId);
        stop.changeStatus(next);
        return stop;
    }
}
