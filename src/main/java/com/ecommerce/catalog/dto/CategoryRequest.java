package com.ecommerce.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 140)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "slug must be lowercase, hyphen-separated alphanumerics")
        String slug,
        @Size(max = 500) String description,
        Long parentId
) {
}
