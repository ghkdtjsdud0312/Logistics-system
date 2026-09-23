package com.logistics.domain.dispatch.application;

import com.logistics.domain.anomaly.application.AnomalyService;
import com.logistics.domain.anomaly.domain.AnomalyType;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchRepository;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.domain.DispatchStatusHistory;
import com.logistics.domain.dispatch.domain.DispatchStatusHistoryRepository;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 배차 유스케이스 - 상태 변경 + 이력 기록 (TASK-008)
 */
@Service
@RequiredArgsConstructor
public class DispatchStatusService {

    private static final String DEFAULT_ACTOR = "OPERATOR";

    private final DispatchService dispatchService;
    private final DispatchRepository dispatchRepository;
    private final DispatchStatusHistoryRepository historyRepository;
    private final AnomalyService anomalyService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(cacheNames = "dispatchDetail", key = "#dispatchId")
    public Dispatch changeStatus(Long dispatchId, DispatchStatus next, Long expectedVersion, String actor, String description) {
        Dispatch dispatch = dispatchService.getDispatch(dispatchId);
        if (!dispatch.getVersion().equals(expectedVersion)) {
            throw new BusinessException(ErrorCode.DISPATCH_STALE_VERSION);
        }

        DispatchStatus previous = dispatch.getStatus();
        try {
            dispatch.changeStatus(next);
        } catch (BusinessException e) {
            if (e.getErrorCode() == ErrorCode.DISPATCH_INVALID_STATUS_TRANSITION) {
                anomalyService.record(AnomalyType.INVALID_TRANSITION, dispatchId, String.valueOf(dispatchId),
                        previous + " -> " + next + " 전이는 허용되지 않습니다.");
            }
            throw e;
        }

        historyRepository.save(DispatchStatusHistory.builder()
                .dispatchId(dispatchId)
                .fromStatus(previous)
                .toStatus(next)
                .actor(actor == null || actor.isBlank() ? DEFAULT_ACTOR : actor)
                .description(description)
                .build());

        // 즉시 flush해 반환된 엔티티의 version이 호출자가 다음 요청에 쓸 최신 값을 갖도록 한다.
        dispatchRepository.flush();
        eventPublisher.publishEvent(new DispatchStatusChangedEvent(dispatchId, previous, next, dispatch.getVersion()));
        return dispatch;
    }

    @Transactional(readOnly = true)
    public List<DispatchStatusHistory> getHistory(Long dispatchId) {
        return historyRepository.findAllByDispatchId(dispatchId);
    }
}
