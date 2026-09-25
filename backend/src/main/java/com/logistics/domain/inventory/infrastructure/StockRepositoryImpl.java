package com.logistics.domain.inventory.infrastructure;

import com.logistics.domain.inventory.domain.Stock;
import com.logistics.domain.inventory.domain.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository jpaRepository;

    @Override
    public Stock save(Stock stock) {
        return jpaRepository.save(stock);
    }

    @Override
    public Optional<Stock> findByProductIdAndLocationId(Long productId, Long locationId) {
        return jpaRepository.findByProductIdAndLocationId(productId, locationId);
    }

    @Override
    public List<Stock> findAll() {
        return jpaRepository.findAll();
    }
}
