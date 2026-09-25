package com.logistics.domain.master.application;

import com.logistics.domain.master.domain.Product;
import com.logistics.domain.master.domain.ProductRepository;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public Map<Long, Product> getProductMap(Collection<Long> ids) {
        return productRepository.findAllByIds(ids).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    public List<Product> search(String keyword) {
        return productRepository.search(keyword);
    }

    @Transactional
    public Product create(Product product) {
        if (productRepository.existsByCode(product.getCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_CODE);
        }
        return productRepository.save(product);
    }
}
