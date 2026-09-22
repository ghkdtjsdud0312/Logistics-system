package com.logistics.domain.delivery.domain;

import com.logistics.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long dispatchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    private double currentLatitude;
    private double currentLongitude;

    @Builder
    public Delivery(Long dispatchId) {
        this.dispatchId = dispatchId;
        this.status = DeliveryStatus.DEPARTED;
    }

    public void updateLocation(double latitude, double longitude) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
        this.status = DeliveryStatus.ON_THE_WAY;
    }

    public void complete() {
        this.status = DeliveryStatus.COMPLETED;
    }
}
