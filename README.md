# Product Catalog API

A Spring Boot REST API for managing an e-commerce product catalog: products, categories, and inventory. Built with Spring Boot 3.3.4, Java 17, JPA/Hibernate, and PostgreSQL, with OpenAPI/Swagger UI for interactive documentation.

## Features

- **Products** — full CRUD plus paginated search with free-text query, category/brand/status filters, price range, and `inStock` filter.
- **Categories** — taxonomy with unique slugs and CRUD operations.
- **Inventory** — per-product stock management: replace inventory, adjust by signed delta, automatic `ACTIVE`/`OUT_OF_STOCK` status sync.
- **Validation** — Jakarta Bean Validation on all request DTOs.
- **Global error handling** — consistent `ApiError` response shape for 400/404/409.
- **OpenAPI 3** — interactive Swagger UI at `/swagger-ui.html`.
- **Seed data** — sample categories, products, and inventory loaded on startup.

## Tech Stack

- Java 17
- Spring Boot 3.3.4 (Web, Data JPA, Validation)
- PostgreSQL (runtime), H2 (tests)
- springdoc-openapi 2.6.0
- Lombok
- Maven

## Prerequisites

- JDK 17+
- Maven 3.9+
- PostgreSQL 13+ running locally (or set `DB_USERNAME` / `DB_PASSWORD` env vars to point elsewhere)

## Setup

1. Create the database:

   ```sql
   CREATE DATABASE product_catalog;
   ```

2. (Optional) Override credentials via environment variables — defaults are `postgres` / `123456789`:

   ```bash
   export DB_USERNAME=postgres
   export DB_PASSWORD=yourpassword
   ```

3. Run the app:

   ```bash
   mvn spring-boot:run
   ```

   The server starts on `http://localhost:8080`.

## Documentation

Once running, open:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI spec: <http://localhost:8080/v3/api-docs>

## API Overview

Base path: `/api/v1`

### Products — `/api/v1/products`

| Method | Path                | Description                              |
|--------|---------------------|------------------------------------------|
| POST   | `/`                 | Create a product (and initial inventory) |
| GET    | `/{id}`             | Get a product by id                      |
| GET    | `/`                 | Search with filters, sort, pagination    |
| PUT    | `/{id}`             | Update a product                         |
| DELETE | `/{id}`             | Delete a product                         |

Search query parameters: `q`, `category`, `categoryId`, `brand`, `status`, `minPrice`, `maxPrice`, `inStock`, `page`, `size`, `sort`.

Examples:

```
GET /api/v1/products?category=electronics&sort=price,asc
GET /api/v1/products?q=phone&minPrice=200&maxPrice=800&inStock=true
GET /api/v1/products?brand=acme&status=ACTIVE&sort=name,asc&page=0&size=20
```

### Categories — `/api/v1/categories`

| Method | Path     | Description           |
|--------|----------|-----------------------|
| POST   | `/`      | Create a category     |
| GET    | `/{id}`  | Get a category by id  |
| GET    | `/`      | List categories       |
| PUT    | `/{id}`  | Update a category     |
| DELETE | `/{id}`  | Delete a category     |

### Inventory — `/api/v1/products/{productId}/inventory`

| Method | Path        | Description                                  |
|--------|-------------|----------------------------------------------|
| GET    | `/`         | Get inventory for a product                  |
| PUT    | `/`         | Replace inventory (qty, reserved, threshold) |
| POST   | `/adjust`   | Adjust stock by signed `delta` query param   |

## Project Structure

```
src/main/java/com/ecommerce/catalog/
├── config/         OpenAPI configuration and seed data
├── controller/     REST controllers
├── domain/         JPA entities (Product, Category, Inventory, ProductStatus)
├── dto/            Request/response DTOs and ApiError
├── exception/      Domain exceptions and global handler
├── mapper/         Entity ↔ DTO mappers
├── repository/     Spring Data JPA repositories
├── search/         Specifications for dynamic product search
└── service/        Business logic
```

## Build

```bash
mvn clean package        # build executable jar
mvn test                 # run tests
java -jar target/product-catalog-0.0.1-SNAPSHOT.jar
```

## Configuration

Defaults live in `src/main/resources/application.yml`. Common overrides:

| Property              | Default                                          | Env var       |
|-----------------------|--------------------------------------------------|---------------|
| `spring.datasource.url`      | `jdbc:postgresql://localhost:5432/product_catalog` | —             |
| `spring.datasource.username` | `postgres`                                       | `DB_USERNAME` |
| `spring.datasource.password` | `123456789`                                      | `DB_PASSWORD` |
| `server.port`                | `8080`                                           | —             |

## License

This project is for educational/demo purposes.
