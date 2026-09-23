package com.logistics.domain.vehicle.domain;

/**
 * 차량 적재 한도 검증 순수 함수
 */
public final class CapacityValidator {

    private CapacityValidator() {
    }

    public static boolean fits(Vehicle vehicle, double totalWeightKg, double totalVolumeM3) {
        return totalWeightKg <= vehicle.getMaxWeightKg() && totalVolumeM3 <= vehicle.getMaxVolumeM3();
    }

    public static double weightRatio(Vehicle vehicle, double totalWeightKg) {
        return totalWeightKg / vehicle.getMaxWeightKg();
    }

    public static double volumeRatio(Vehicle vehicle, double totalVolumeM3) {
        return totalVolumeM3 / vehicle.getMaxVolumeM3();
    }
}
