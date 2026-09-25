package com.logistics.domain.inventory.infrastructure;

import com.logistics.domain.inventory.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductIdAndLocationId(Long productId, Long locationId);

    List<Stock> findAllByProductId(Long productId);
}
