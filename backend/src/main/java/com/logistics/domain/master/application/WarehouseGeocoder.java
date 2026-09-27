package com.logistics.domain.master.application;

import com.logistics.domain.master.domain.Warehouse;
import com.logistics.global.geocoding.Coordinate;
import com.logistics.global.geocoding.GeocodingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 창고 주소를 좌표로 변환해 창고에 기록한다. 변환에 실패하면 좌표만 비운다. */
@Component
@RequiredArgsConstructor
public class WarehouseGeocoder {

    private final GeocodingClient geocodingClient;

    public void locate(Warehouse warehouse, String address) {
        String normalized = address == null || address.isBlank() ? null : address.trim();
        Coordinate coordinate = geocodingClient.geocode(normalized).orElse(null);
        warehouse.relocate(normalized, coordinate == null ? null : coordinate.latitude(),
                coordinate == null ? null : coordinate.longitude());
    }
}
