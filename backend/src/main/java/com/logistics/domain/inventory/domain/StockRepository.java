package com.logistics.domain.inventory.domain;

import java.util.List;
import java.util.Optional;

public interface StockRepository {

    Stock save(Stock stock);

    Optional<Stock> findByProductIdAndLocationId(Long productId, Long locationId);

    List<Stock> findAll();
}
