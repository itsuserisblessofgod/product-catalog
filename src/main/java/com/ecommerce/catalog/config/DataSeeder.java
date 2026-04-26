package com.ecommerce.catalog.config;

import com.ecommerce.catalog.domain.Category;
import com.ecommerce.catalog.domain.Inventory;
import com.ecommerce.catalog.domain.Product;
import com.ecommerce.catalog.domain.ProductStatus;
import com.ecommerce.catalog.repository.CategoryRepository;
import com.ecommerce.catalog.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Configuration
@Profile("!test")
public class DataSeeder {

    @Bean
    CommandLineRunner seedCatalog(CategoryRepository categories, ProductRepository products) {
        return args -> {
            if (categories.count() > 0 || products.count() > 0) {
                return;
            }

            Category electronics = categories.save(Category.builder()
                    .name("Electronics").slug("electronics")
                    .description("Phones, laptops, and accessories").build());
            Category books = categories.save(Category.builder()
                    .name("Books").slug("books")
                    .description("Printed and digital books").build());

            saveProduct(products, "SKU-IPH-15-256", "iPhone 15 Pro 256GB",
                    "Apple smartphone with A17 Pro chip", new BigDecimal("1199.00"),
                    "Apple", electronics, 25);
            saveProduct(products, "SKU-MBA-M3-512", "MacBook Air M3 512GB",
                    "13-inch laptop with Apple M3", new BigDecimal("1499.00"),
                    "Apple", electronics, 10);
            saveProduct(products, "SKU-PIX-9-128", "Pixel 9 128GB",
                    "Google smartphone with Tensor G4", new BigDecimal("799.00"),
                    "Google", electronics, 0);
            saveProduct(products, "SKU-BOOK-CLEAN-CODE", "Clean Code",
                    "Robert C. Martin", new BigDecimal("39.99"),
                    "Prentice Hall", books, 100);
        };
    }

    private void saveProduct(ProductRepository repo, String sku, String name, String description,
                             BigDecimal price, String brand, Category category, int qty) {
        Product product = Product.builder()
                .sku(sku).name(name).description(description)
                .price(price).currency("USD").brand(brand)
                .status(qty > 0 ? ProductStatus.ACTIVE : ProductStatus.OUT_OF_STOCK)
                .category(category)
                .build();
        Inventory inv = Inventory.builder()
                .product(product).quantity(qty).build();
        product.setInventory(inv);
        repo.save(product);
    }
}
