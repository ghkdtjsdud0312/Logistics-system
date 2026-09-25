package com.logistics.domain.inventory.application;

import com.logistics.domain.inventory.domain.Stock;
import com.logistics.domain.inventory.domain.StockRepository;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 재고 변경 유스케이스. 다른 도메인(입고, 주문, 피킹, 반품)이 호출한다. */
@Service
@RequiredArgsConstructor
@Transactional
public class StockService {

    private final StockRepository stockRepository;

    /** 적치·반품입고 등으로 위치 재고를 늘린다. 재고 행이 없으면 만든다. */
    public Stock increase(Long productId, Long locationId, int quantity) {
        Stock stock = stockRepository.findByProductIdAndLocationId(productId, locationId)
                .orElseGet(() -> new Stock(productId, locationId));
        stock.increase(quantity);
        return stockRepository.save(stock);
    }

    /** 피킹 완료: 예약분을 현재재고에서 함께 차감한다. */
    public Stock consumeReserved(Long productId, Long locationId, int quantity) {
        Stock stock = stockRepository.findByProductIdAndLocationId(productId, locationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSUFFICIENT_STOCK));
        stock.consumeReserved(quantity);
        return stockRepository.save(stock);
    }
}
