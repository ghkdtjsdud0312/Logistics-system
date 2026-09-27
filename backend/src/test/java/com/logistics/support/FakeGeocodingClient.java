package com.logistics.support;

import com.logistics.global.geocoding.Coordinate;
import com.logistics.global.geocoding.GeocodingClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** 테스트용 좌표 변환기. 등록한 주소만 좌표를 돌려주고 나머지는 변환 실패로 취급한다. */
@Component
@Profile("test")
public class FakeGeocodingClient implements GeocodingClient {

    private final Map<String, Coordinate> known = new ConcurrentHashMap<>();

    public void register(String address, double latitude, double longitude) {
        known.put(address, new Coordinate(latitude, longitude));
    }

    public void clear() {
        known.clear();
    }

    @Override
    public Optional<Coordinate> geocode(String address) {
        return Optional.ofNullable(address).map(known::get);
    }
}
