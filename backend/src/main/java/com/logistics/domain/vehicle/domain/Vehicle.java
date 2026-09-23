package com.logistics.domain.vehicle.domain;

import com.logistics.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "vehicle")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vehicle extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vehicleNumber;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false)
    private double maxWeightKg;

    @Column(nullable = false)
    private double maxVolumeM3;

    /** 허브(출발지)로부터의 거리(km). 후보 정렬 패널티 계산용 */
    @Column(nullable = false)
    private double hubDistanceKm;

    /** 정비/비활성 등 배차 불가 상태만 표현한다. 개별 배차 점유 여부는 배차 시간대 겹침으로 판단한다. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Builder
    public Vehicle(String vehicleNumber, String vehicleType, double maxWeightKg, double maxVolumeM3, double hubDistanceKm) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.maxWeightKg = maxWeightKg;
        this.maxVolumeM3 = maxVolumeM3;
        this.hubDistanceKm = hubDistanceKm;
        this.status = VehicleStatus.AVAILABLE;
    }

    /** 정비 등록/해제 등 운영자가 차량 가용 상태를 직접 전환할 때 사용 */
    public void changeStatus(VehicleStatus status) {
        this.status = status;
    }
}
