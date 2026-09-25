package com.logistics.domain.inventory.presentation.dto;

import com.logistics.domain.inventory.domain.Stock;
import com.logistics.domain.master.application.LocationInfo;
import com.logistics.domain.master.domain.Product;

import java.util.Comparator;

public record StockResponse(
        String warehouseName,
        String zoneCode,
        String locationCode,
        String productCode,
        String productName,
        int onHand,
        int reserved,
        int available
) {
    public static final Comparator<StockResponse> BY_LOCATION =
            Comparator.comparing(StockResponse::locationCode).thenComparing(StockResponse::productCode);

    public static StockResponse of(Stock stock, Product product, LocationInfo location) {
        return new StockResponse(location.warehouseName(), location.zoneCode(), location.locationCode(),
                product.getCode(), product.getName(), stock.getOnHand(), stock.getReserved(), stock.getAvailable());
    }
}
