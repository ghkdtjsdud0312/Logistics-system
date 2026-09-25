package com.logistics.domain.master.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** 창고 → 구역 → 위치 계층의 Aggregate Root */
@Getter
@Entity
@Table(name = "warehouse")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Warehouse extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("code")
    private List<Zone> zones = new ArrayList<>();

    public Warehouse(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Zone addZone(String zoneCode, String zoneName) {
        if (zones.stream().anyMatch(z -> z.getCode().equals(zoneCode))) {
            throw new BusinessException(ErrorCode.DUPLICATE_CODE);
        }
        Zone zone = new Zone(this, zoneCode, zoneName);
        zones.add(zone);
        return zone;
    }

    public Zone getZone(String zoneCode) {
        return zones.stream().filter(z -> z.getCode().equals(zoneCode)).findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ZONE_NOT_FOUND));
    }

    public Zone findZone(Long zoneId) {
        return zones.stream().filter(z -> z.getId().equals(zoneId)).findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ZONE_NOT_FOUND));
    }
}
