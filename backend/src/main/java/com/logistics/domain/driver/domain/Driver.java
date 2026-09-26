package com.logistics.domain.driver.domain;

import com.logistics.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "driver")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Driver extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String driverCode;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    @Builder
    public Driver(String driverCode, String name, String phone) {
        this.driverCode = driverCode;
        this.name = name;
        this.phone = phone;
        this.status = DriverStatus.AVAILABLE;
    }

    public void update(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void changeStatus(DriverStatus status) {
        this.status = status;
    }
}
