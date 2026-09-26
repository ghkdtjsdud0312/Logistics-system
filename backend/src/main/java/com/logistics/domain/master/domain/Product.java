package com.logistics.domain.master.domain;

import com.logistics.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String unit;

    /** 단위 중량(kg). 배차 적재량 검증에 쓰인다. */
    @Column(nullable = false)
    private double unitWeightKg;

    @Column(nullable = false)
    private boolean active = true;

    @Builder
    public Product(String code, String name, String unit, double unitWeightKg) {
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.unitWeightKg = unitWeightKg;
    }

    public void update(String name, String unit, double unitWeightKg) {
        this.name = name;
        this.unit = unit;
        this.unitWeightKg = unitWeightKg;
    }
}
