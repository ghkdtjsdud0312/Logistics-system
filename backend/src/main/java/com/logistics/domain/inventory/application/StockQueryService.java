package com.logistics.domain.inventory.application;

import com.logistics.domain.inventory.domain.Stock;
import com.logistics.domain.inventory.domain.StockRepository;
import com.logistics.domain.inventory.presentation.dto.StockResponse;
import com.logistics.domain.master.application.LocationInfo;
import com.logistics.domain.master.application.LocationQueryService;
import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** 재고 현황 조회. 창고·위치·상품 이름은 master 서비스로 조회한다(ID 참조). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockQueryService {

    private final StockRepository stockRepository;
    private final ProductService productService;
    private final LocationQueryService locationQueryService;

    public List<StockResponse> search(Long warehouseId, Long zoneId, String keyword, StockStatus status) {
        Map<Long, LocationInfo> locations = locationQueryService.getLocationInfoMap();
        Set<Long> productIds = keyword == null || keyword.isBlank() ? null
                : productService.search(keyword).stream().map(Product::getId).collect(Collectors.toSet());
        List<Stock> stocks = stockRepository.findAll().stream()
                .filter(s -> productIds == null || productIds.contains(s.getProductId()))
                .filter(s -> matchesLocation(locations.get(s.getLocationId()), warehouseId, zoneId))
                .filter(s -> status == null || status.matches(s.getAvailable()))
                .toList();
        Map<Long, Product> products = productService.getProductMap(
                stocks.stream().map(Stock::getProductId).collect(Collectors.toSet()));
        return stocks.stream()
                .map(s -> StockResponse.of(s, products.get(s.getProductId()), locations.get(s.getLocationId())))
                .sorted(StockResponse.BY_LOCATION)
                .toList();
    }

    private boolean matchesLocation(LocationInfo info, Long warehouseId, Long zoneId) {
        return info != null && (warehouseId == null || info.warehouseId().equals(warehouseId))
                && (zoneId == null || info.zoneId().equals(zoneId));
    }
}
