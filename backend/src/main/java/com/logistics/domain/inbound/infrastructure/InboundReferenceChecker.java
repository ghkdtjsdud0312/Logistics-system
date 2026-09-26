package com.logistics.domain.inbound.infrastructure;

import com.logistics.global.reference.JpqlReferenceChecker;
import com.logistics.global.reference.ReferenceType;
import org.springframework.stereotype.Component;

@Component
public class InboundReferenceChecker extends JpqlReferenceChecker {

    @Override
    public boolean isReferenced(ReferenceType type, Long id) {
        return switch (type) {
            case PRODUCT -> exists("Inbound", "productId", id);
            case LOCATION -> exists("Inbound", "locationId", id);
            default -> false;
        };
    }
}
