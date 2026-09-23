package com.logistics.domain.anomaly.infrastructure;

import com.logistics.domain.anomaly.domain.Anomaly;
import com.logistics.domain.anomaly.domain.AnomalyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnomalyJpaRepository extends JpaRepository<Anomaly, Long> {

    List<Anomaly> findAllByStatus(AnomalyStatus status);

    boolean existsByFingerprintAndStatus(String fingerprint, AnomalyStatus status);
}
