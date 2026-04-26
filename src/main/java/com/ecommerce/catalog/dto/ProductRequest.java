package com.ecommerce.catalog.dto;

import com.ecommerce.catalog.domain.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 64) String sku,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description,
        @NotNull @DecimalMin(value = "0.00", inclusive = true)
        @Digits(integer = 10, fraction = 2) BigDecimal price,
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be ISO 4217 (e.g., USD)")
        String currency,
        @Size(max = 120) String brand,
        ProductStatus status,
        @NotNull Long categoryId,
        @PositiveOrZero Integer initialQuantity
) {
}
