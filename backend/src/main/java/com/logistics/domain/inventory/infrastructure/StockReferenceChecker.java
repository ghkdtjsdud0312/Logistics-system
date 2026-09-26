package com.logistics.domain.inventory.infrastructure;

import com.logistics.global.reference.JpqlReferenceChecker;
import com.logistics.global.reference.ReferenceType;
import org.springframework.stereotype.Component;

@Component
public class StockReferenceChecker extends JpqlReferenceChecker {

    @Override
    public boolean isReferenced(ReferenceType type, Long id) {
        return switch (type) {
            case PRODUCT -> exists("Stock", "productId", id);
            case LOCATION -> exists("Stock", "locationId", id);
            default -> false;
        };
    }
}
