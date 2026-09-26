package com.logistics.domain.dispatch.infrastructure;

import com.logistics.global.reference.JpqlReferenceChecker;
import com.logistics.global.reference.ReferenceType;
import org.springframework.stereotype.Component;

@Component
public class DispatchReferenceChecker extends JpqlReferenceChecker {

    @Override
    public boolean isReferenced(ReferenceType type, Long id) {
        return switch (type) {
            case VEHICLE -> exists("Dispatch", "vehicleId", id);
            case DRIVER -> exists("Dispatch", "driverId", id);
            default -> false;
        };
    }
}
