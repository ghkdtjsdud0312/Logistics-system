package com.logistics.domain.audit.application;

import com.logistics.domain.audit.domain.AuditLogRepository;
import com.logistics.domain.order.application.OrderEventProvider;
import com.logistics.domain.order.application.OrderEventView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 주문 상세의 이벤트 이력과 타임라인 시각을 감사로그로 채운다. */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditOrderEventProvider implements OrderEventProvider {

    private final AuditLogRepository auditLogRepository;

    @Override
    public List<OrderEventView> findByOrderId(Long orderId) {
        return auditLogRepository.findByOrderId(orderId).stream().map(log -> {
            String label = AuditActionLabels.of(log.getTargetType(), log.getAction());
            boolean isOrder = "ORDER".equals(log.getTargetType());
            return new OrderEventView(log.getOccurredAt(), isOrder ? label : label + " (" + log.getTargetNo() + ")",
                    isOrder ? log.getToStatus() : null);
        }).toList();
    }
}
