package com.logistics.domain.anomaly.infrastructure;

import com.logistics.domain.anomaly.domain.Anomaly;
import com.logistics.domain.anomaly.domain.AnomalyRepository;
import com.logistics.domain.anomaly.domain.AnomalyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AnomalyRepositoryImpl implements AnomalyRepository {

    private final AnomalyJpaRepository jpaRepository;

    @Override
    public Anomaly save(Anomaly anomaly) {
        return jpaRepository.save(anomaly);
    }

    @Override
    public Optional<Anomaly> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Anomaly> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Anomaly> findAllByStatus(AnomalyStatus status) {
        return jpaRepository.findAllByStatus(status);
    }

    @Override
    public boolean existsByFingerprintAndStatus(String fingerprint, AnomalyStatus status) {
        return jpaRepository.existsByFingerprintAndStatus(fingerprint, status);
    }
}
