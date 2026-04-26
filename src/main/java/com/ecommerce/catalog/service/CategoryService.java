package com.ecommerce.catalog.service;

import com.ecommerce.catalog.domain.Category;
import com.ecommerce.catalog.dto.CategoryRequest;
import com.ecommerce.catalog.dto.CategoryResponse;
import com.ecommerce.catalog.exception.DuplicateResourceException;
import com.ecommerce.catalog.exception.ResourceNotFoundException;
import com.ecommerce.catalog.mapper.CategoryMapper;
import com.ecommerce.catalog.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException("Category slug already exists: " + request.slug());
        }
        Category parent = resolveParent(request.parentId(), null);
        Category saved = categoryRepository.save(categoryMapper.toEntity(request, parent));
        return categoryMapper.toResponse(saved);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getEntity(id);
        if (!category.getSlug().equals(request.slug())
                && categoryRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException("Category slug already exists: " + request.slug());
        }
        Category parent = resolveParent(request.parentId(), id);
        categoryMapper.updateEntity(category, request, parent);
        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(Long id) {
        return categoryMapper.toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> list(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toResponse);
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    Category getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    private Category resolveParent(Long parentId, Long selfId) {
        if (parentId == null) {
            return null;
        }
        if (selfId != null && parentId.equals(selfId)) {
            throw new IllegalArgumentException("Category cannot be its own parent");
        }
        return categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent category not found: " + parentId));
    }
}
