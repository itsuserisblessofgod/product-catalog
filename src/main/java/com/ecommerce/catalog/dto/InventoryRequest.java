package com.ecommerce.catalog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record InventoryRequest(
        @NotNull @PositiveOrZero Integer quantity,
        @PositiveOrZero Integer reservedQuantity,
        @PositiveOrZero Integer lowStockThreshold,
        @Size(max = 120) String warehouseLocation
) {
}
