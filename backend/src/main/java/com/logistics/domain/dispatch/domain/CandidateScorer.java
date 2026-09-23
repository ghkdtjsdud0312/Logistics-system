package com.logistics.domain.dispatch.domain;

/**
 * 차량 후보 정렬 점수 계산 순수 함수 (DOMAIN.md 배차 가능성 규칙)
 * - 점수가 낮을수록 적합. 자동 확정하지 않고 후보 정렬에만 사용한다.
 */
public final class CandidateScorer {

    private static final double HUB_DISTANCE_NORMALIZER_KM = 100.0;

    private CandidateScorer() {
    }

    public static double score(double weightRatio, double volumeRatio, double hubDistanceKm) {
        double unusedWeightRatio = 1 - weightRatio;
        double unusedVolumeRatio = 1 - volumeRatio;
        double distancePenalty = hubDistanceKm / HUB_DISTANCE_NORMALIZER_KM;
        return unusedWeightRatio + unusedVolumeRatio + distancePenalty;
    }
}
