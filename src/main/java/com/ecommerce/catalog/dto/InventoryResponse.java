package com.ecommerce.catalog.dto;

import java.time.Instant;

public record InventoryResponse(
        Long id,
        Long productId,
        String productSku,
        Integer quantity,
        Integer reservedQuantity,
        Integer availableQuantity,
        Integer lowStockThreshold,
        boolean lowStock,
        String warehouseLocation,
        Instant updatedAt
) {
}
