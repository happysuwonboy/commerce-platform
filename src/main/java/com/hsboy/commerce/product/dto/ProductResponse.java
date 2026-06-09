package com.hsboy.commerce.product.dto;

import com.hsboy.commerce.product.Product;
import com.hsboy.commerce.product.ProductStatus;

import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        int price,
        String description,
        String imageUrl,
        Long categoryId,
        String categoryName,
        ProductStatus status,
        LocalDateTime createdAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getImageUrl(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getStatus(),
                product.getCreatedAt()
        );
    }
}