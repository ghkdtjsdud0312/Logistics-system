package com.logistics.global.geocoding;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Optional;

/** OpenStreetMap Nominatim으로 주소를 좌표로 바꾼다(키 불필요). 공용 서버 정책에 따라 초당 1건 이하로 호출한다. */
@Slf4j
@Component
@Profile("!test")
public class NominatimGeocodingClient implements GeocodingClient {

    private static final long MIN_INTERVAL_MS = 1100;

    private final RestClient client;
    private long lastCallAt;

    public NominatimGeocodingClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(4));
        this.client = RestClient.builder().baseUrl("https://nominatim.openstreetmap.org")
                .requestFactory(factory).defaultHeader("User-Agent", "logistics-system-demo/1.0").build();
    }

    @Override
    public synchronized Optional<Coordinate> geocode(String address) {
        if (address == null || address.isBlank()) {
            return Optional.empty();
        }
        try {
            waitForTurn();
            JsonNode body = client.get().uri(b -> b.path("/search").queryParam("q", address.trim())
                    .queryParam("format", "json").queryParam("limit", 1).queryParam("countrycodes", "kr").build())
                    .retrieve().body(JsonNode.class);
            JsonNode first = body == null ? null : body.path(0);
            if (first == null || first.isMissingNode()) {
                return Optional.empty();
            }
            return Optional.of(new Coordinate(first.path("lat").asDouble(), first.path("lon").asDouble()));
        } catch (Exception e) {
            log.warn("Nominatim 좌표 변환 실패: {} ({})", address, e.getMessage());
            return Optional.empty();
        }
    }

    private void waitForTurn() throws InterruptedException {
        long wait = lastCallAt + MIN_INTERVAL_MS - System.currentTimeMillis();
        if (wait > 0) {
            Thread.sleep(wait);
        }
        lastCallAt = System.currentTimeMillis();
    }
}
