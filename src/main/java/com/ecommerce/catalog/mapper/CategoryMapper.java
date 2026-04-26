package com.ecommerce.catalog.mapper;

import com.ecommerce.catalog.domain.Category;
import com.ecommerce.catalog.dto.CategoryRequest;
import com.ecommerce.catalog.dto.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request, Category parent) {
        return Category.builder()
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .parent(parent)
                .build();
    }

    public void updateEntity(Category category, CategoryRequest request, Category parent) {
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());
        category.setParent(parent);
    }

    public CategoryResponse toResponse(Category category) {
        Category parent = category.getParent();
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                parent != null ? parent.getId() : null,
                parent != null ? parent.getSlug() : null
        );
    }
}
