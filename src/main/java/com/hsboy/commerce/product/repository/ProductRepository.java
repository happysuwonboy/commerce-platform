package com.hsboy.commerce.product.repository;

import com.hsboy.commerce.product.Product;
import com.hsboy.commerce.product.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatusAndIdLessThanOrderByIdDesc(
            ProductStatus status,
            Long cursorId,
            Pageable pageable
    );

}
