package com.ecommerce.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload to create or update a category")
public record CategoryRequest(
        @Schema(example = "Electronics") @NotBlank @Size(max = 120) String name,
        @Schema(example = "electronics") @NotBlank @Size(max = 140)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "slug must be lowercase, hyphen-separated alphanumerics")
        String slug,
        @Schema(example = "Phones, laptops, and accessories") @Size(max = 500) String description,
        @Schema(example = "null", nullable = true) Long parentId
) {
}
