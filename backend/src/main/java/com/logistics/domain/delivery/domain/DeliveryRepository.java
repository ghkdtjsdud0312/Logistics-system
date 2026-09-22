package com.logistics.domain.delivery.domain;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository {

    Delivery save(Delivery delivery);

    Optional<Delivery> findById(Long id);

    List<Delivery> findAll();
}
