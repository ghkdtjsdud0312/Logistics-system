package com.logistics.domain.inventory.infrastructure;

import com.logistics.domain.inventory.domain.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockReservationJpaRepository extends JpaRepository<StockReservation, Long> {

    List<StockReservation> findAllByOrderIdOrderById(Long orderId);
}
