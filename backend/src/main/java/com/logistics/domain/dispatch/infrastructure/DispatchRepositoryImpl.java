package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DispatchRepositoryImpl implements DispatchRepository {

    private final DispatchJpaRepository dispatchJpaRepository;

    @Override
    public Dispatch save(Dispatch dispatch) {
        return dispatchJpaRepository.save(dispatch);
    }

    @Override
    public Optional<Dispatch> findById(Long id) {
        return dispatchJpaRepository.findById(id);
    }

    @Override
    public List<Dispatch> findAll() {
        return dispatchJpaRepository.findAll();
    }
}
