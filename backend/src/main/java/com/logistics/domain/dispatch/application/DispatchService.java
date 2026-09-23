package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchRepository;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 배차 조회 유스케이스
 * - 배차 생성(후보 조회/확정)은 DispatchCandidateService, DispatchConfirmService
 * - 상태 변경/경로 계산은 DispatchStatusService, RouteOptimizationService 참고
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DispatchService {

    private final DispatchRepository dispatchRepository;

    public Dispatch getDispatch(Long id) {
        return dispatchRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DISPATCH_NOT_FOUND));
    }

    public List<Dispatch> getDispatchList() {
        return dispatchRepository.findAll();
    }

    /** 컨트롤러의 DTO 변환 시점에는 트랜잭션이 끝나 있으므로, 지연 로딩되는 stops를 미리 초기화해서 반환한다. */
    public Dispatch getDispatchWithStops(Long id) {
        Dispatch dispatch = getDispatch(id);
        Hibernate.initialize(dispatch.getStops());
        return dispatch;
    }
}
