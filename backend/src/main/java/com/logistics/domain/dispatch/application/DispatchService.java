package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchRepository;
import com.logistics.domain.dispatch.domain.NearestNeighborDispatchOptimizer;
import com.logistics.domain.dispatch.domain.Waypoint;
import com.logistics.domain.dispatch.presentation.dto.DispatchCreateRequest;
import com.logistics.domain.dispatch.presentation.dto.WaypointDto;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 배차 유스케이스
 * - 배차 생성 시 NearestNeighborDispatchOptimizer로 경로를 즉시 최적화
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final NearestNeighborDispatchOptimizer optimizer;

    @Transactional
    public Dispatch createDispatch(DispatchCreateRequest request) {
        List<Waypoint> waypoints = request.waypoints().stream()
                .map(WaypointDto::toEntity)
                .toList();

        Dispatch dispatch = Dispatch.builder()
                .driverName(request.driverName())
                .vehicleNumber(request.vehicleNumber())
                .waypoints(waypoints)
                .build();

        List<Waypoint> optimizedRoute = optimizer.optimize(waypoints);
        if (optimizedRoute.isEmpty()) {
            throw new BusinessException(ErrorCode.DISPATCH_OPTIMIZATION_FAILED);
        }
        dispatch.applyOptimizedRoute(optimizedRoute);

        return dispatchRepository.save(dispatch);
    }

    public Dispatch getDispatch(Long id) {
        return dispatchRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DISPATCH_NOT_FOUND));
    }

    public List<Dispatch> getDispatchList() {
        return dispatchRepository.findAll();
    }

    @Transactional
    public Dispatch startDispatch(Long id) {
        Dispatch dispatch = getDispatch(id);
        dispatch.start();
        return dispatch;
    }

    @Transactional
    public Dispatch completeDispatch(Long id) {
        Dispatch dispatch = getDispatch(id);
        dispatch.complete();
        return dispatch;
    }
}
