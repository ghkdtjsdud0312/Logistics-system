package com.logistics.domain.master.application;

/** 다른 도메인이 위치 ID로 창고·구역·위치 이름을 조회할 때 쓰는 읽기 전용 정보 */
public record LocationInfo(
        Long locationId,
        String locationCode,
        Long zoneId,
        String zoneCode,
        Long warehouseId,
        String warehouseName
) {
}
