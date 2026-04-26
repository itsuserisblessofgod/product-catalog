package com.ecommerce.catalog.service;

import com.ecommerce.catalog.domain.Category;
import com.ecommerce.catalog.domain.Product;
import com.ecommerce.catalog.dto.ProductRequest;
import com.ecommerce.catalog.dto.ProductResponse;
import com.ecommerce.catalog.exception.DuplicateResourceException;
import com.ecommerce.catalog.exception.ResourceNotFoundException;
import com.ecommerce.catalog.mapper.ProductMapper;
import com.ecommerce.catalog.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository,
                          CategoryService categoryService,
                          ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }

    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product SKU already exists: " + request.sku());
        }
        Category category = categoryService.getEntity(request.categoryId());
        Product saved = productRepository.save(productMapper.toEntity(request, category));
        return productMapper.toResponse(saved);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getEntity(id);
        if (!product.getSku().equals(request.sku())
                && productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product SKU already exists: " + request.sku());
        }
        Category category = categoryService.getEntity(request.categoryId());
        productMapper.updateEntity(product, request, category);
        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return productMapper.toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(Specification<Product> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }
}
