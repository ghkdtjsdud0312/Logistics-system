package com.logistics.domain.outbound.application;

/** 출고 계획 목록의 합산 적재량 - 다른 도메인(Dispatch)이 OutboundService를 통해서만 얻는 값 */
public record OutboundLoadSummary(double totalWeightKg, double totalVolumeM3) {
}
