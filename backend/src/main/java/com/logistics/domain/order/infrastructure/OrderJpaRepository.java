package com.logistics.domain.order.infrastructure;

import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query("SELECT o.status AS status, COUNT(o) AS count FROM Order o "
            + "WHERE o.orderedAt >= :from AND o.orderedAt < :to GROUP BY o.status")
    List<StatusCount> countByStatus(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    interface StatusCount {

        OrderStatus getStatus();

        long getCount();
    }
}
