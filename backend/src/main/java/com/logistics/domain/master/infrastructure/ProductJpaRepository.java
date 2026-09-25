package com.logistics.domain.master.infrastructure;

import com.logistics.domain.master.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    boolean existsByCode(String code);

    @Query("SELECT p FROM Product p WHERE p.code LIKE %:keyword% OR p.name LIKE %:keyword% ORDER BY p.code")
    List<Product> search(@Param("keyword") String keyword);
}
