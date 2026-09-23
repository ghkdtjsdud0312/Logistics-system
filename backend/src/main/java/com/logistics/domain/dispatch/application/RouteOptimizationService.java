package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.*;
import com.logistics.domain.outbound.application.OutboundService;
import com.logistics.domain.outbound.domain.Outbound;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 배차 유스케이스 - 경로 최적화 (TASK-007)
 * - 허브에서 출발해 Nearest Neighbor로 초기 경로를 만들고 2-opt로 개선한다.
 */
@Service
@RequiredArgsConstructor
public class RouteOptimizationService {

    private static final String ALGORITHM_NAME = "nearest-neighbor+2opt";

    private final DispatchService dispatchService;
    private final DispatchRepository dispatchRepository;
    private final OutboundService outboundService;
    private final HubLocation hubLocation;
    private final NearestNeighborDispatchOptimizer nearestNeighborOptimizer;
    private final TwoOptRouteImprover twoOptImprover;

    @Transactional
    @CacheEvict(cacheNames = "dispatchDetail", key = "#dispatchId")
    public Dispatch optimize(Long dispatchId) {
        Dispatch dispatch = dispatchService.getDispatch(dispatchId);
        List<Waypoint> input = buildInputRoute(dispatch);

        List<Waypoint> initialRoute = nearestNeighborOptimizer.optimize(input);
        double initialDistanceKm = RouteDistanceCalculator.totalDistanceKm(initialRoute);

        List<Waypoint> optimizedRoute = twoOptImprover.improve(initialRoute);
        double optimizedDistanceKm = RouteDistanceCalculator.totalDistanceKm(optimizedRoute);

        dispatch.applyRoute(ALGORITHM_NAME, initialDistanceKm, optimizedDistanceKm, optimizedRoute);
        // RouteStop은 IDENTITY 생성 전략이라 flush 전까지 id가 없다. save(merge) 대신 flush만 강제해
        // 이미 관리 중인 dispatch를 불필요하게 merge하지 않고 호출자가 stop.id를 바로 쓸 수 있게 한다.
        dispatchRepository.flush();
        return dispatch;
    }

    private List<Waypoint> buildInputRoute(Dispatch dispatch) {
        List<Waypoint> stops = dispatch.getOutboundIds().stream()
                .sorted()
                .map(outboundService::getOutbound)
                .map(this::toWaypoint)
                .toList();

        List<Waypoint> route = new ArrayList<>();
        route.add(hubLocation.toWaypoint());
        route.addAll(stops);
        return route;
    }

    private Waypoint toWaypoint(Outbound outbound) {
        return new Waypoint(outbound.getId(), outbound.getDestination(), outbound.getLatitude(), outbound.getLongitude());
    }
}
