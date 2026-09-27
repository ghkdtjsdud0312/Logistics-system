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

/** 배차 응답의 출발지·방문지 좌표와 출발지 지정 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DispatchRouteViewTest {

    @Autowired private DispatchRegistrationService registrationService;
    @Autowired private DispatchLoadingService loadingDispatchService;
    @Autowired private DispatchRouteService routeService;
    @Autowired private DispatchQueryService queryService;
    @Autowired private LoadingService loadingService;
    @Autowired private WarehouseService warehouseService;
    @Autowired private FakeGeocodingClient geocoder;
    @Autowired private TestData testData;
    @Autowired private TestFlow testFlow;

    @AfterEach
    void cleanUp() {
        geocoder.clear();
    }

    private Long shipment(String address) {
        return loadingService.load(List.of(testFlow.packedOrder(0.5, 2, address))).get(0).getId();
    }

    private Dispatch dispatch(Long warehouseId, Long... shipmentIds) {
        return registrationService.register(new RegisterDispatchCommand(testData.vehicle(1000), testData.driver(),
                warehouseId, LocalDateTime.now(), LocalDateTime.now().plusHours(2), List.of(shipmentIds)));
    }

    private ErrorCode errorOf(Runnable action) {
        return catchThrowableOfType(action::run, BusinessException.class).getErrorCode();
    }

    @Test
    @DisplayName("응답에 출발지와 방문 순서대로의 배송지 좌표가 담기고, 좌표를 못 얻은 배송지는 null이다")
    void originAndStops() {
        geocoder.register("본사", 37.5, 127.0);
        geocoder.register("가까운곳", 37.5, 127.1);
        Long warehouse = warehouseService.createWarehouse("V" + System.nanoTime(), "본사창고", "본사").getId();
        Long near = shipment("가까운곳");
        Long lost = shipment("알 수 없는 곳");

        DispatchResponse response = queryService.toResponse(dispatch(warehouse, near, lost));

        assertThat(response.origin().name()).isEqualTo("본사창고");
        assertThat(response.origin().latitude()).isEqualTo(37.5);
        assertThat(response.stops()).extracting("shipmentId").containsExactly(near, lost);
        assertThat(response.stops().get(0).longitude()).isEqualTo(127.1);
        assertThat(response.stops().get(1).latitude()).isNull();
        assertThat(response.totalDistanceKm()).isNull();
    }

    @Test
    @DisplayName("출발지가 없던 적재중 배차에 출발지를 지정하면 경로 최적화를 할 수 있고, 없는 창고나 마감된 배차는 거부한다")
    void changeOrigin() {
        geocoder.register("본사", 37.5, 127.0);
        geocoder.register("가까운곳", 37.5, 127.1);
        geocoder.register("먼곳", 37.5, 127.3);
        Long warehouse = warehouseService.createWarehouse("V" + System.nanoTime(), "본사창고", "본사").getId();
        Dispatch dispatch = dispatch(null, shipment("먼곳"), shipment("가까운곳"));
        assertThat(errorOf(() -> routeService.optimize(dispatch.getId()))).isEqualTo(ErrorCode.ROUTE_ORIGIN_MISSING);
        assertThat(errorOf(() -> routeService.changeOrigin(dispatch.getId(), 999_999L)))
                .isEqualTo(ErrorCode.WAREHOUSE_NOT_FOUND);

        routeService.changeOrigin(dispatch.getId(), warehouse);
        assertThat(routeService.optimize(dispatch.getId())).isEmpty();
        DispatchResponse response = queryService.toResponse(dispatch);
        assertThat(response.warehouseId()).isEqualTo(warehouse);
        assertThat(response.totalDistanceKm()).isNotNull();

        loadingDispatchService.close(dispatch.getId());
        assertThat(errorOf(() -> routeService.changeOrigin(dispatch.getId(), warehouse)))
                .isEqualTo(ErrorCode.DISPATCH_NOT_LOADING);
    }
}
