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

    /** 적재량(kg). 배차 등록 시 Shipment 총 중량과 비교한다. */
    @Column(nullable = false)
    private double capacityKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Builder
    public Vehicle(String vehicleNumber, String vehicleType, double capacityKg) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.capacityKg = capacityKg;
        this.status = VehicleStatus.AVAILABLE;
    }

    public void update(String vehicleType, double capacityKg) {
        this.vehicleType = vehicleType;
        this.capacityKg = capacityKg;
    }

    public void changeStatus(VehicleStatus status) {
        this.status = status;
    }
}
