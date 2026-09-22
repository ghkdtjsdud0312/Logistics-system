package com.logistics.domain.delivery.infrastructure;

import com.logistics.domain.delivery.domain.Delivery;
import com.logistics.domain.delivery.domain.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final DeliveryJpaRepository deliveryJpaRepository;

    @Override
    public Delivery save(Delivery delivery) {
        return deliveryJpaRepository.save(delivery);
    }

    @Override
    public Optional<Delivery> findById(Long id) {
        return deliveryJpaRepository.findById(id);
    }

    @Override
    public List<Delivery> findAll() {
        return deliveryJpaRepository.findAll();
    }
}
