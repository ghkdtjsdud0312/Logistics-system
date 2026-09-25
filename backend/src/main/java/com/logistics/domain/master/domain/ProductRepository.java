package com.logistics.domain.master.domain;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAllByIds(java.util.Collection<Long> ids);

    boolean existsByCode(String code);

    /** keyword가 없으면 전체, 있으면 코드/이름 부분 일치 */
    List<Product> search(String keyword);
}
