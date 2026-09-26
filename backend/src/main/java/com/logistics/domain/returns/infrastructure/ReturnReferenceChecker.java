package com.logistics.domain.returns.infrastructure;

import com.logistics.global.reference.JpqlReferenceChecker;
import com.logistics.global.reference.ReferenceType;
import org.springframework.stereotype.Component;

@Component
public class ReturnReferenceChecker extends JpqlReferenceChecker {

    @Override
    public boolean isReferenced(ReferenceType type, Long id) {
        return type == ReferenceType.LOCATION && exists("ReturnOrder", "locationId", id);
    }
}
