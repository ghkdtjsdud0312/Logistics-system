package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.application.DispatchLoadingService;
import com.logistics.domain.dispatch.application.DispatchQueryService;
import com.logistics.domain.dispatch.application.DispatchRegistrationService;
import com.logistics.domain.dispatch.application.DispatchRouteService;
import com.logistics.domain.dispatch.application.RegisterDispatchCommand;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.presentation.dto.DispatchResponse;
import com.logistics.domain.loading.application.LoadingService;
import com.logistics.domain.master.application.WarehouseService;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.support.FakeGeocodingClient;
import com.logistics.support.TestData;
import com.logistics.support.TestFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DispatchRouteTest {

    @Autowired private DispatchRegistrationService registrationService;
    @Autowired private DispatchLoadingService loadingDispatchService;
    @Autowired private DispatchRouteService routeService;
    @Autowired private DispatchQueryService queryService;
    @Autowired private LoadingService loadingService;
    @Autowired private WarehouseService warehouseService;
    @Autowired private FakeGeocodingClient geocoder;
    @Autowired private TestData testData;
    @Autowired private TestFlow testFlow;

    private Long far;
    private Long near;
    private Long mid;
    private Long warehouseId;

    @BeforeEach
    void setUp() {
        geocoder.register("본사", 37.5, 127.0);
        geocoder.register("먼곳", 37.5, 127.3);
        geocoder.register("가까운곳", 37.5, 127.1);
        geocoder.register("중간곳", 37.5, 127.2);
        warehouseId = warehouseService.createWarehouse("R" + System.nanoTime(), "본사창고", "본사").getId();
        far = shipment("먼곳");
        near = shipment("가까운곳");
        mid = shipment("중간곳");
    }

    @AfterEach
    void cleanUp() {
        geocoder.clear();
    }

    private Long shipment(String address) {
        return loadingService.load(List.of(testFlow.packedOrder(0.5, 2, address))).get(0).getId();
    }

    private Dispatch dispatch(Long warehouse) {
        return registrationService.register(new RegisterDispatchCommand(testData.vehicle(1000), testData.driver(),
                warehouse, LocalDateTime.now(), LocalDateTime.now().plusHours(2), List.of(far, near, mid)));
    }

    private ErrorCode errorOf(Runnable action) {
        return catchThrowableOfType(action::run, BusinessException.class).getErrorCode();
    }

    @Test
    @DisplayName("담은 순서가 방문 순서가 되고, 경로 최적화하면 가까운 곳부터 방문하도록 바뀌며 총 거리가 줄어든다")
    void optimize() {
        Dispatch dispatch = dispatch(warehouseId);
        DispatchResponse before = queryService.toResponse(dispatch);
        assertThat(before.shipmentIds()).containsExactly(far, near, mid);

        assertThat(routeService.optimize(dispatch.getId())).isEmpty();
        DispatchResponse after = queryService.toResponse(dispatch);
        assertThat(after.shipmentIds()).containsExactly(near, mid, far);
        assertThat(after.totalDistanceKm()).isLessThan(before.totalDistanceKm());
        assertThat(after.warehouseId()).isEqualTo(warehouseId);
    }

    @Test
    @DisplayName("방문 순서를 수동으로 바꿀 수 있고, 담긴 배송과 다른 목록은 거부하며, 마감 후에는 바꿀 수 없다")
    void reorder() {
        Dispatch dispatch = dispatch(warehouseId);
        routeService.reorder(dispatch.getId(), List.of(mid, far, near));
        assertThat(queryService.toResponse(dispatch).shipmentIds()).containsExactly(mid, far, near);

        assertThat(errorOf(() -> routeService.reorder(dispatch.getId(), List.of(mid, far))))
                .isEqualTo(ErrorCode.INVALID_ROUTE_ORDER);
        assertThat(errorOf(() -> routeService.reorder(dispatch.getId(), List.of(mid, mid, far))))
                .isEqualTo(ErrorCode.INVALID_ROUTE_ORDER);

        loadingDispatchService.close(dispatch.getId());
        assertThat(errorOf(() -> routeService.reorder(dispatch.getId(), List.of(far, near, mid))))
                .isEqualTo(ErrorCode.DISPATCH_NOT_LOADING);
        assertThat(errorOf(() -> routeService.optimize(dispatch.getId()))).isEqualTo(ErrorCode.DISPATCH_NOT_LOADING);
    }

    @Test
    @DisplayName("출발지가 없거나 좌표가 없으면 최적화를 거부하고, 좌표를 못 얻은 배송은 맨 뒤에 두며 총 거리는 null이다")
    void missingCoordinates() {
        Dispatch noOrigin = dispatch(null);
        assertThat(errorOf(() -> routeService.optimize(noOrigin.getId()))).isEqualTo(ErrorCode.ROUTE_ORIGIN_MISSING);
        assertThat(queryService.toResponse(noOrigin).totalDistanceKm()).isNull();

        Long lost = shipment("알 수 없는 곳");
        Long farAgain = shipment("먼곳");
        Long nearAgain = shipment("가까운곳");
        Dispatch dispatch = registrationService.register(new RegisterDispatchCommand(testData.vehicle(1000),
                testData.driver(), warehouseId, LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                List.of(lost, farAgain, nearAgain)));
        assertThat(routeService.optimize(dispatch.getId())).containsExactly(lost);
        assertThat(queryService.toResponse(dispatch).shipmentIds()).containsExactly(nearAgain, farAgain, lost);
        assertThat(queryService.toResponse(dispatch).totalDistanceKm()).isNull();
    }

    @Test
    @DisplayName("화물을 제외하면 남은 화물의 순서는 유지되고 나중에 담으면 맨 뒤에 붙는다")
    void removeAndAdd() {
        Dispatch dispatch = dispatch(warehouseId);
        loadingDispatchService.removeShipment(dispatch.getId(), near);
        assertThat(queryService.toResponse(dispatch).shipmentIds()).containsExactly(far, mid);
        loadingDispatchService.addShipments(dispatch.getId(), List.of(near));
        assertThat(queryService.toResponse(dispatch).shipmentIds()).containsExactly(far, mid, near);
    }
}
