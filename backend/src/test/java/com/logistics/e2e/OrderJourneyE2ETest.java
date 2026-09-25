package com.logistics.e2e;

import com.jayway.jsonpath.JsonPath;
import com.logistics.domain.dashboard.application.DashboardCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ORD-001 하나가 시스템 전체를 관통하는 시나리오를 API만으로 검증한다.
 * 입고·적치 → 주문(재고 예약) → 출고 지시 → 피킹 → 포장 → 상차 → 배차 → 배송 → 완료 / 실패 → 반품.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderJourneyE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private DashboardCache dashboardCache;

    @BeforeEach
    void clearCache() {
        dashboardCache.evictAll();
    }

    private String call(HttpMethod method, String path, String body) throws Exception {
        MockHttpServletRequestBuilder builder = request(method, path);
        if (body != null) {
            builder.contentType(MediaType.APPLICATION_JSON).content(body);
        }
        return mockMvc.perform(builder).andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
    }

    private long id(String json) {
        Object data = JsonPath.read(json, "$.data");
        return ((Number) (data instanceof java.util.Map<?, ?> m ? m.get("id") : data)).longValue();
    }

    private void assertStock(int onHand, int reserved, int available) throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/api/stocks"))
                .andExpect(jsonPath("$.data[0].onHand").value(onHand))
                .andExpect(jsonPath("$.data[0].reserved").value(reserved))
                .andExpect(jsonPath("$.data[0].available").value(available));
    }

    /** 상품·창고·차량·기사를 만들고 입고 100개를 적치한 뒤 [상품ID, 위치ID]를 돌려준다. */
    private long[] prepareStock() throws Exception {
        long productId = id(call(HttpMethod.POST, "/api/products",
                "{\"code\":\"WATER001\",\"name\":\"생수 500ml\",\"unit\":\"EA\",\"unitWeightKg\":0.5}"));
        long warehouseId = id(call(HttpMethod.POST, "/api/warehouses", "{\"code\":\"A\",\"name\":\"A창고\"}"));
        long zoneId = id(call(HttpMethod.POST, "/api/warehouses/" + warehouseId + "/zones", "{\"code\":\"A01\",\"name\":\"A구역\"}"));
        long locationId = id(call(HttpMethod.POST, "/api/zones/" + zoneId + "/locations", "{\"code\":\"A-01-01\"}"));
        call(HttpMethod.POST, "/api/vehicles", "{\"vehicleNumber\":\"12가1234\",\"vehicleType\":\"1톤\",\"capacityKg\":1000}");
        call(HttpMethod.POST, "/api/drivers", "{\"driverCode\":\"D001\",\"name\":\"홍길동\",\"phone\":\"010\"}");
        long inboundId = id(call(HttpMethod.POST, "/api/inbounds", "{\"partnerName\":\"거래처\",\"productId\":" + productId
                + ",\"quantity\":100,\"inboundDate\":\"2026-09-25\"}"));
        call(HttpMethod.PATCH, "/api/inbounds/" + inboundId + "/receive", null);
        call(HttpMethod.PATCH, "/api/inbounds/" + inboundId + "/putaway", "{\"locationId\":" + locationId + "}");
        return new long[]{productId, locationId};
    }

    /** 주문을 만들어 포장완료까지 진행하고 주문 ID를 돌려준다. */
    private long orderUntilPacked(long productId, int quantity) throws Exception {
        long orderId = id(call(HttpMethod.POST, "/api/orders", "{\"customerName\":\"김철수\",\"address\":\"서울시 강남구\","
                + "\"phone\":\"010\",\"items\":[{\"productId\":" + productId + ",\"quantity\":" + quantity + "}]}"));
        call(HttpMethod.POST, "/api/orders/" + orderId + "/release", null);
        String tasks = call(HttpMethod.GET, "/api/picking-tasks?status=WAITING", null);
        long taskId = ((Number) JsonPath.read(tasks, "$.data[0].id")).longValue();
        call(HttpMethod.PATCH, "/api/picking-tasks/" + taskId + "/start", null);
        call(HttpMethod.PATCH, "/api/picking-tasks/" + taskId + "/complete", "{\"pickedQty\":" + quantity + "}");
        String packing = call(HttpMethod.GET, "/api/packing-tasks?status=WAITING", null);
        long packId = ((Number) JsonPath.read(packing, "$.data[0].id")).longValue();
        call(HttpMethod.PATCH, "/api/packing-tasks/" + packId + "/complete", "{\"boxCode\":\"BOX-001\"}");
        return orderId;
    }

    /** 상차 → 배차 → 배송 시작까지 진행하고 Shipment ID를 돌려준다. */
    private long loadAndDispatch(long orderId) throws Exception {
        String loaded = call(HttpMethod.POST, "/api/loadings", "{\"orderIds\":[" + orderId + "]}");
        long shipmentId = ((Number) JsonPath.read(loaded, "$.data[0].id")).longValue();
        long vehicleId = ((Number) JsonPath.read(call(HttpMethod.GET, "/api/vehicles", null), "$.data[0].id")).longValue();
        long driverId = ((Number) JsonPath.read(call(HttpMethod.GET, "/api/drivers", null), "$.data[0].id")).longValue();
        long dispatchId = id(call(HttpMethod.POST, "/api/dispatches", "{\"vehicleId\":" + vehicleId + ",\"driverId\":" + driverId
                + ",\"plannedStartAt\":\"2026-09-25T13:00:00\",\"plannedArrivalAt\":\"2026-09-25T15:00:00\",\"shipmentIds\":[" + shipmentId + "]}"));
        call(HttpMethod.PATCH, "/api/dispatches/" + dispatchId + "/start", null);
        return shipmentId;
    }

    @Test
    @DisplayName("ORD-001: 입고 → 주문 → 피킹 → 포장 → 상차 → 배차 → 배송완료까지 수량과 재고가 규칙대로 변한다")
    void orderJourney_delivered() throws Exception {
        long[] base = prepareStock();
        assertStock(100, 0, 100);

        long orderId = id(call(HttpMethod.POST, "/api/orders", "{\"customerName\":\"김철수\",\"address\":\"서울시 강남구\","
                + "\"phone\":\"010\",\"items\":[{\"productId\":" + base[0] + ",\"quantity\":10}]}"));
        assertStock(100, 10, 90);
        call(HttpMethod.POST, "/api/orders/" + orderId + "/release", null);
        String tasks = call(HttpMethod.GET, "/api/picking-tasks?status=WAITING", null);
        long taskId = ((Number) JsonPath.read(tasks, "$.data[0].id")).longValue();
        call(HttpMethod.PATCH, "/api/picking-tasks/" + taskId + "/start", null);
        call(HttpMethod.PATCH, "/api/picking-tasks/" + taskId + "/complete", "{\"pickedQty\":10}");
        assertStock(90, 0, 90);
        String packing = call(HttpMethod.GET, "/api/packing-tasks?status=WAITING", null);
        call(HttpMethod.PATCH, "/api/packing-tasks/" + ((Number) JsonPath.read(packing, "$.data[0].id")).longValue()
                + "/complete", "{\"boxCode\":\"BOX-001\"}");

        long shipmentId = loadAndDispatch(orderId);
        mockMvc.perform(request(HttpMethod.GET, "/api/orders/" + orderId)).andExpect(jsonPath("$.data.status").value("IN_DELIVERY"));
        call(HttpMethod.PATCH, "/api/shipments/" + shipmentId + "/deliver", "{\"deliveredQty\":10}");

        mockMvc.perform(request(HttpMethod.GET, "/api/orders/" + orderId))
                .andExpect(jsonPath("$.data.status").value("DELIVERED"))
                .andExpect(jsonPath("$.data.items[0].orderedQty").value(10))
                .andExpect(jsonPath("$.data.items[0].pickedQty").value(10))
                .andExpect(jsonPath("$.data.items[0].loadedQty").value(10))
                .andExpect(jsonPath("$.data.items[0].deliveredQty").value(10))
                .andExpect(jsonPath("$.data.timeline[6].done").value(true))
                .andExpect(jsonPath("$.data.delivery.driverName").value("홍길동"));
        assertStock(90, 0, 90);
        mockMvc.perform(request(HttpMethod.GET, "/api/dashboard/summary"))
                .andExpect(jsonPath("$.data.delivered").value(1))
                .andExpect(jsonPath("$.data.inDelivery").value(0));
        mockMvc.perform(request(HttpMethod.GET, "/api/vehicles")).andExpect(jsonPath("$.data[0].status").value("AVAILABLE"));
    }

    @Test
    @DisplayName("배송 실패 → 반품 자동 생성 → 회수 → 반품입고로 재고가 복구되고 처리완료까지 진행된다")
    void orderJourney_failedAndReturned() throws Exception {
        long[] base = prepareStock();
        long orderId = orderUntilPacked(base[0], 5);
        assertStock(95, 0, 95);
        long shipmentId = loadAndDispatch(orderId);

        call(HttpMethod.PATCH, "/api/shipments/" + shipmentId + "/fail", "{\"reason\":\"CUSTOMER_ABSENT\",\"detail\":\"부재\"}");
        mockMvc.perform(request(HttpMethod.GET, "/api/orders/" + orderId)).andExpect(jsonPath("$.data.status").value("FAILED"));
        String returns = call(HttpMethod.GET, "/api/returns?status=REQUESTED", null);
        long returnId = ((Number) JsonPath.read(returns, "$.data[0].id")).longValue();

        call(HttpMethod.PATCH, "/api/returns/" + returnId + "/collect", null);
        call(HttpMethod.PATCH, "/api/returns/" + returnId + "/collected", null);
        call(HttpMethod.PATCH, "/api/returns/" + returnId + "/receive", "{\"locationId\":" + base[1] + "}");
        assertStock(100, 0, 100);
        call(HttpMethod.PATCH, "/api/returns/" + returnId + "/complete", null);
        mockMvc.perform(request(HttpMethod.GET, "/api/returns/" + returnId)).andExpect(jsonPath("$.data.status").value("COMPLETED"));
        mockMvc.perform(request(HttpMethod.GET, "/api/dashboard/summary")).andExpect(jsonPath("$.data.failed").value(1));
    }
}
