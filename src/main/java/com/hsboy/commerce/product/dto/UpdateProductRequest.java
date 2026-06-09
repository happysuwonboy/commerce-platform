package com.hsboy.commerce.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateProductRequest(
        @NotBlank String name,
        @Min(0) Integer price,
        String description,
        String imageUrl
) {}
