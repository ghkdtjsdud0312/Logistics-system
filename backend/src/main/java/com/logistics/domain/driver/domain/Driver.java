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

    @Column(nullable = false)
    private String name;

    /** 휴무 등 배차 불가 상태만 표현한다. 개별 배차 점유 여부는 배차 시간대 겹침으로 판단한다. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    @Builder
    public Driver(String name) {
        this.name = name;
        this.status = DriverStatus.AVAILABLE;
    }

    /** 휴무 등록/해제 등 운영자가 기사 가용 상태를 직접 전환할 때 사용 */
    public void changeStatus(DriverStatus status) {
        this.status = status;
    }
}
