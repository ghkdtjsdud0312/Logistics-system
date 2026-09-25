package com.logistics.domain.inventory.infrastructure;

import com.logistics.domain.inventory.domain.StockReservation;
import com.logistics.domain.inventory.domain.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StockReservationRepositoryImpl implements StockReservationRepository {

    private final StockReservationJpaRepository jpaRepository;

    @Override
    public StockReservation save(StockReservation reservation) {
        return jpaRepository.save(reservation);
    }

    @Override
    public List<StockReservation> findAllByOrderId(Long orderId) {
        return jpaRepository.findAllByOrderIdOrderById(orderId);
    }
}
