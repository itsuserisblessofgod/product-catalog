package com.ecommerce.catalog.search;

import com.ecommerce.catalog.domain.Product;
import com.ecommerce.catalog.domain.ProductStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> build(ProductSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (c.q() != null && !c.q().isBlank()) {
                String pattern = "%" + c.q().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("sku")), pattern)
                ));
            }

            if (c.categorySlug() != null && !c.categorySlug().isBlank()) {
                Join<Object, Object> category = root.join("category");
                predicates.add(cb.equal(cb.lower(category.get("slug")), c.categorySlug().toLowerCase()));
            }

            if (c.categoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), c.categoryId()));
            }

            if (c.brand() != null && !c.brand().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), c.brand().toLowerCase()));
            }

            if (c.status() != null) {
                predicates.add(cb.equal(root.get("status"), c.status()));
            }

            BigDecimal min = c.minPrice();
            BigDecimal max = c.maxPrice();
            if (min != null && max != null) {
                predicates.add(cb.between(root.get("price"), min, max));
            } else if (min != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), min));
            } else if (max != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), max));
            }

            if (Boolean.TRUE.equals(c.inStock())) {
                Join<Object, Object> inv = root.join("inventory");
                predicates.add(cb.greaterThan(
                        cb.diff(inv.get("quantity"), inv.get("reservedQuantity")),
                        0));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public record ProductSearchCriteria(
            String q,
            String categorySlug,
            Long categoryId,
            String brand,
            ProductStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock
    ) {
    }
}
