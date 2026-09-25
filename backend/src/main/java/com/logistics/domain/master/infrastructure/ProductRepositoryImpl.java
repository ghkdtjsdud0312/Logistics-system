package com.logistics.domain.master.infrastructure;

import com.logistics.domain.master.domain.Product;
import com.logistics.domain.master.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    public Product save(Product product) {
        return jpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Product> findAllByIds(java.util.Collection<Long> ids) {
        return jpaRepository.findAllById(ids);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public List<Product> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return jpaRepository.findAll(Sort.by("code"));
        }
        return jpaRepository.search(keyword.trim());
    }
}
