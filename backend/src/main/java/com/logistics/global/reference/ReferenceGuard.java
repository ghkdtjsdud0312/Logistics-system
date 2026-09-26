package com.logistics.global.reference;

import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/** 기준정보 삭제 전에 다른 도메인이 사용 중인지 검사한다. */
@Component
@RequiredArgsConstructor
public class ReferenceGuard {

    private final List<ReferenceChecker> checkers;

    public void assertNotReferenced(ReferenceType type, Long id) {
        assertNoneReferenced(type, List.of(id));
    }

    public void assertNoneReferenced(ReferenceType type, Collection<Long> ids) {
        boolean used = ids.stream().anyMatch(id -> checkers.stream().anyMatch(c -> c.isReferenced(type, id)));
        if (used) {
            throw new BusinessException(ErrorCode.IN_USE);
        }
    }
}
