package com.logistics.global.geocoding;

import java.util.Optional;

/** 주소를 좌표로 바꾼다. 실패하거나 지원하지 않으면 빈 값을 돌려주고 예외를 던지지 않는다. */
public interface GeocodingClient {

    Optional<Coordinate> geocode(String address);
}
