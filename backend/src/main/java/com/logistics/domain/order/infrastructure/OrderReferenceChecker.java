package com.logistics.domain.order.infrastructure;

import com.logistics.global.reference.JpqlReferenceChecker;
import com.logistics.global.reference.ReferenceType;
import org.springframework.stereotype.Component;

@Component
public class OrderReferenceChecker extends JpqlReferenceChecker {

    @Override
    public boolean isReferenced(ReferenceType type, Long id) {
        return type == ReferenceType.PRODUCT && exists("OrderItem", "productId", id);
    }
}
