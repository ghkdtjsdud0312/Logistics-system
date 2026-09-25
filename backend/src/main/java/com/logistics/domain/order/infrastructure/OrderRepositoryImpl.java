package com.logistics.domain.order.infrastructure;

import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderRepository;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        return jpaRepository.saveAndFlush(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Order> search(OrderSearchCriteria c) {
        PageRequest page = PageRequest.of(c.page(), c.size(), Sort.by(Sort.Direction.DESC, "id"));
        return jpaRepository.findAll(OrderSpecifications.from(c), page).getContent();
    }
}
