package com.logistics.domain.inventory.domain;

import java.util.List;

public interface StockReservationRepository {

    StockReservation save(StockReservation reservation);

    List<StockReservation> findAllByOrderId(Long orderId);
}
