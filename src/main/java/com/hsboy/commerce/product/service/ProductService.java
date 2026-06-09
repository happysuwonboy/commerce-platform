package com.hsboy.commerce.product.service;

import com.hsboy.commerce.product.Category;
import com.hsboy.commerce.product.Product;
import com.hsboy.commerce.product.ProductStatus;
import com.hsboy.commerce.product.dto.CreateProductRequest;
import com.hsboy.commerce.product.dto.ProductResponse;
import com.hsboy.commerce.product.dto.UpdateProductRequest;
import com.hsboy.commerce.product.exception.CategoryNotFoundException;
import com.hsboy.commerce.product.exception.ProductNotFoundException;
import com.hsboy.commerce.product.repository.CategoryRepository;
import com.hsboy.commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
         Product product = productRepository.findById(id)
                 .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Product product = Product.create(request.name(), request.price(), request.description(),
                request.imageUrl(), category, ProductStatus.ACTIVE);

        productRepository.save(product);

        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.update(request.name(), request.price(), request.description(), request.imageUrl());

        return ProductResponse.from(product);
    }
}
