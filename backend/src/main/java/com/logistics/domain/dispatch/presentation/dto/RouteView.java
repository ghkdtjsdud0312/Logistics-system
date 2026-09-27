package com.logistics.domain.dispatch.presentation.dto;

import java.util.List;

/** 배차 응답에 담기는 경로 정보: 출발지, 방문 순서대로의 배송지, 직선거리 합(km). */
public record RouteView(Origin origin, List<Stop> stops, Double totalDistanceKm) {

    public record Origin(String name, Double latitude, Double longitude) {
    }

    public record Stop(Long shipmentId, String orderNo, String address, Double latitude, Double longitude) {
    }
}
