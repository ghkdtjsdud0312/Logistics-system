package com.logistics.domain.anomaly.application;

import com.logistics.domain.anomaly.domain.*;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 이상 탐지 기록 유스케이스
 * - record()는 호출부(배차 확정/상태변경 등)의 트랜잭션이 롤백되어도 감사 기록이 남도록 REQUIRES_NEW로 실행한다.
 */
@Service
@RequiredArgsConstructor
public class AnomalyService {

    private final AnomalyRepository anomalyRepository;

    /**
     * @param dispatchId   이미 확정된 배차가 있으면 그 ID, 확정 자체가 거부된 경우(아직 Dispatch가 없음)엔 null
     * @param fingerprintKey 중복 억제 기준 키 (배차 확정 전이면 dispatchId가 없으므로 별도로 받는다. 예: outboundIds 조합)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(AnomalyType type, Long dispatchId, String fingerprintKey, String message) {
        String fingerprint = type + ":" + fingerprintKey;
        if (anomalyRepository.existsByFingerprintAndStatus(fingerprint, AnomalyStatus.OPEN)) {
            return;
        }
        anomalyRepository.save(Anomaly.builder()
                .type(type)
                .dispatchId(dispatchId)
                .fingerprint(fingerprint)
                .message(message)
                .build());
    }

    @Transactional(readOnly = true)
    public List<Anomaly> getAnomalies(AnomalyStatus status) {
        return status == null ? anomalyRepository.findAll() : anomalyRepository.findAllByStatus(status);
    }

    @Transactional
    public Anomaly changeStatus(Long id, AnomalyStatus next) {
        Anomaly anomaly = anomalyRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANOMALY_NOT_FOUND));
        anomaly.changeStatus(next);
        return anomaly;
    }
}
