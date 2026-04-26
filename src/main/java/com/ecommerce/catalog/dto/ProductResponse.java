package com.ecommerce.catalog.dto;

import com.ecommerce.catalog.domain.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String currency,
        String brand,
        ProductStatus status,
        Long categoryId,
        String categorySlug,
        Integer availableQuantity,
        Instant createdAt,
        Instant updatedAt
) {
}
