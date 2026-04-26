package com.ecommerce.catalog.dto;

import com.ecommerce.catalog.domain.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Payload to create or update a product")
public record ProductRequest(
        @Schema(example = "SKU-IPH-15-256") @NotBlank @Size(max = 64) String sku,
        @Schema(example = "iPhone 15 Pro 256GB") @NotBlank @Size(max = 200) String name,
        @Schema(example = "Apple smartphone with A17 Pro chip") @Size(max = 2000) String description,
        @Schema(example = "1199.00") @NotNull @DecimalMin(value = "0.00", inclusive = true)
        @Digits(integer = 10, fraction = 2) BigDecimal price,
        @Schema(example = "USD")
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be ISO 4217 (e.g., USD)")
        String currency,
        @Schema(example = "Apple") @Size(max = 120) String brand,
        @Schema(example = "ACTIVE") ProductStatus status,
        @Schema(example = "1") @NotNull Long categoryId,
        @Schema(example = "50") @PositiveOrZero Integer initialQuantity
) {
}
