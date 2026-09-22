package com.logistics.domain.delivery.infrastructure;

import com.logistics.domain.delivery.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryJpaRepository extends JpaRepository<Delivery, Long> {
}
