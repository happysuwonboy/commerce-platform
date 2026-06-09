package com.hsboy.commerce.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        @NotBlank String name,
        @Min(0) int price,
        String description,
        String imageUrl,
        @NotNull Long categoryId
) {}
