package com.logistics.domain.order.infrastructure;

import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderRepository;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import com.logistics.domain.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
    public List<Order> findAllByIds(java.util.Collection<Long> ids) {
        return jpaRepository.findAllById(ids);
    }

    @Override
    public Map<OrderStatus, Long> countByStatus(LocalDateTime from, LocalDateTime to) {
        Map<OrderStatus, Long> counts = new EnumMap<>(OrderStatus.class);
        jpaRepository.countByStatus(from, to).forEach(c -> counts.put(c.getStatus(), c.getCount()));
        return counts;
    }

    @Override
    public List<Order> search(OrderSearchCriteria c) {
        PageRequest page = PageRequest.of(c.page(), c.size(), Sort.by(Sort.Direction.DESC, "id"));
        return jpaRepository.findAll(OrderSpecifications.from(c), page).getContent();
    }
}
