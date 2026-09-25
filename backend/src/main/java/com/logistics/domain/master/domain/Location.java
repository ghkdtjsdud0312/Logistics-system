package com.logistics.domain.master.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "location")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Zone zone;

    /** 창고 전체에서 유일한 위치 코드 (예: A-01-01) */
    @Column(nullable = false, unique = true)
    private String code;

    Location(Zone zone, String code) {
        this.zone = zone;
        this.code = code;
    }
}
