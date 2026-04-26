package com.ecommerce.catalog.mapper;

import com.ecommerce.catalog.domain.Category;
import com.ecommerce.catalog.domain.Inventory;
import com.ecommerce.catalog.domain.Product;
import com.ecommerce.catalog.domain.ProductStatus;
import com.ecommerce.catalog.dto.ProductRequest;
import com.ecommerce.catalog.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request, Category category) {
        Product product = Product.builder()
                .sku(request.sku())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .currency(request.currency() != null ? request.currency() : "USD")
                .brand(request.brand())
                .status(request.status() != null ? request.status() : ProductStatus.DRAFT)
                .category(category)
                .build();

        Inventory inventory = Inventory.builder()
                .product(product)
                .quantity(request.initialQuantity() != null ? request.initialQuantity() : 0)
                .build();
        product.setInventory(inventory);

        return product;
    }

    public void updateEntity(Product product, ProductRequest request, Category category) {
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        if (request.currency() != null) {
            product.setCurrency(request.currency());
        }
        product.setBrand(request.brand());
        if (request.status() != null) {
            product.setStatus(request.status());
        }
        product.setCategory(category);
    }

    public ProductResponse toResponse(Product product) {
        Inventory inv = product.getInventory();
        Integer available = inv != null ? inv.availableQuantity() : 0;
        Category category = product.getCategory();
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCurrency(),
                product.getBrand(),
                product.getStatus(),
                category != null ? category.getId() : null,
                category != null ? category.getSlug() : null,
                available,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
