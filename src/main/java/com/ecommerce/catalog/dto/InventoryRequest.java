package com.ecommerce.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload to set inventory for a product")
public record InventoryRequest(
        @Schema(example = "100") @NotNull @PositiveOrZero Integer quantity,
        @Schema(example = "0") @PositiveOrZero Integer reservedQuantity,
        @Schema(example = "5") @PositiveOrZero Integer lowStockThreshold,
        @Schema(example = "WH-EU-1") @Size(max = 120) String warehouseLocation
) {
}
