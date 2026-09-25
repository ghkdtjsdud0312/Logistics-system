package com.logistics.domain.vehicle.domain;

/**
 * 차량 적재량(kg) 검증 순수 함수. 부피는 검증하지 않는다.
 */
public final class CapacityValidator {

    private CapacityValidator() {
    }

    public static boolean fits(Vehicle vehicle, double totalWeightKg) {
        return totalWeightKg <= vehicle.getCapacityKg();
    }
}
