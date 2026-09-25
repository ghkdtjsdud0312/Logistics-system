package com.logistics.domain.inventory.application;

import com.logistics.domain.inventory.domain.Stock;
import com.logistics.domain.inventory.domain.StockRepository;
import com.logistics.domain.inventory.domain.StockReservation;
import com.logistics.domain.inventory.domain.StockReservationRepository;
import com.logistics.domain.master.application.LocationInfo;
import com.logistics.domain.master.application.LocationQueryService;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** 주문 재고 예약. 위치 코드 순으로 배분하고 예약 내역을 남긴다. */
@Service
@RequiredArgsConstructor
@Transactional
public class StockReservationService {

    private final StockRepository stockRepository;
    private final StockReservationRepository reservationRepository;
    private final LocationQueryService locationQueryService;

    /** 가용재고가 모자라면 아무것도 예약하지 않고 거절한다. */
    public List<StockReservation> reserve(Long orderId, Long orderItemId, Long productId, int quantity) {
        Map<Long, LocationInfo> locations = locationQueryService.getLocationInfoMap();
        List<Stock> candidates = stockRepository.findAllByProductId(productId).stream()
                .filter(s -> s.getAvailable() > 0 && locations.containsKey(s.getLocationId()))
                .sorted(Comparator.comparing(s -> locations.get(s.getLocationId()).locationCode()))
                .toList();
        if (candidates.stream().mapToInt(Stock::getAvailable).sum() < quantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
        int remaining = quantity;
        List<StockReservation> reservations = new ArrayList<>();
        for (Stock stock : candidates) {
            int take = Math.min(remaining, stock.getAvailable());
            stock.reserve(take);
            reservations.add(reservationRepository.save(
                    new StockReservation(orderId, orderItemId, productId, stock.getLocationId(), take)));
            remaining -= take;
            if (remaining == 0) break;
        }
        return reservations;
    }

    @Transactional(readOnly = true)
    public List<StockReservation> getReservations(Long orderId) {
        return reservationRepository.findAllByOrderId(orderId);
    }
}
