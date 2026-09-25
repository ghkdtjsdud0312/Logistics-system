package com.logistics.domain.master.application;

import com.logistics.domain.master.domain.WarehouseRepository;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 위치 ID → 창고·구역·위치 정보 조회 (타 도메인용 Application Service) */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationQueryService {

    private final WarehouseRepository warehouseRepository;

    public Map<Long, LocationInfo> getLocationInfoMap() {
        return warehouseRepository.findAll().stream()
                .flatMap(w -> w.getZones().stream().flatMap(z -> z.getLocations().stream()
                        .map(l -> new LocationInfo(l.getId(), l.getCode(), z.getId(), z.getCode(),
                                w.getId(), w.getName()))))
                .collect(Collectors.toMap(LocationInfo::locationId, Function.identity()));
    }

    public LocationInfo getLocationInfo(Long locationId) {
        LocationInfo info = getLocationInfoMap().get(locationId);
        if (info == null) {
            throw new BusinessException(ErrorCode.LOCATION_NOT_FOUND);
        }
        return info;
    }

    public List<LocationInfo> getAll() {
        return List.copyOf(getLocationInfoMap().values());
    }
}
