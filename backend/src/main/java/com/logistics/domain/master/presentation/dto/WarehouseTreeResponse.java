package com.logistics.domain.master.presentation.dto;

import com.logistics.domain.master.domain.Location;
import com.logistics.domain.master.domain.Warehouse;
import com.logistics.domain.master.domain.Zone;

import java.util.List;

/** 창고 → 구역 → 위치 트리 */
public record WarehouseTreeResponse(Long id, String code, String name, List<ZoneNode> zones) {

    public record ZoneNode(Long id, String code, String name, List<LocationNode> locations) {
    }

    public record LocationNode(Long id, String code) {
    }

    public static WarehouseTreeResponse from(Warehouse warehouse) {
        return new WarehouseTreeResponse(warehouse.getId(), warehouse.getCode(), warehouse.getName(),
                warehouse.getZones().stream().map(WarehouseTreeResponse::zoneNode).toList());
    }

    private static ZoneNode zoneNode(Zone zone) {
        return new ZoneNode(zone.getId(), zone.getCode(), zone.getName(),
                zone.getLocations().stream().map(WarehouseTreeResponse::locationNode).toList());
    }

    private static LocationNode locationNode(Location location) {
        return new LocationNode(location.getId(), location.getCode());
    }
}
