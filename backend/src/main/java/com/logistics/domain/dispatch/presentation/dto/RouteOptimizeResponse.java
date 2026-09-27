package com.logistics.domain.dispatch.presentation.dto;

import java.util.List;

/** 경로 최적화 결과. 좌표를 얻지 못한 배송은 순서 맨 뒤에 두고 ID를 알려 준다. */
public record RouteOptimizeResponse(DispatchResponse dispatch, List<Long> unlocatedShipmentIds) {
}
