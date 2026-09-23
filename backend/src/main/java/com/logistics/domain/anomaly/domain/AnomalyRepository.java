package com.logistics.domain.anomaly.domain;

import java.util.List;
import java.util.Optional;

public interface AnomalyRepository {

    Anomaly save(Anomaly anomaly);

    Optional<Anomaly> findById(Long id);

    List<Anomaly> findAll();

    List<Anomaly> findAllByStatus(AnomalyStatus status);

    boolean existsByFingerprintAndStatus(String fingerprint, AnomalyStatus status);
}
