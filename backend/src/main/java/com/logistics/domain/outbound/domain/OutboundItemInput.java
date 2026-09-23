package com.logistics.domain.outbound.domain;

/**
 * 출고 계획 생성 시 각 품목 한 줄의 입력값 (Application → Domain 전달용)
 */
public record OutboundItemInput(Long inboundId, int quantity, double weightKg, double volumeM3) {
}
