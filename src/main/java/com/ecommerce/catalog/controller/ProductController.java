package com.ecommerce.catalog.controller;

import com.ecommerce.catalog.domain.Product;
import com.ecommerce.catalog.domain.ProductStatus;
import com.ecommerce.catalog.dto.ApiError;
import com.ecommerce.catalog.dto.PageResponse;
import com.ecommerce.catalog.dto.ProductRequest;
import com.ecommerce.catalog.dto.ProductResponse;
import com.ecommerce.catalog.search.ProductSpecifications;
import com.ecommerce.catalog.search.ProductSpecifications.ProductSearchCriteria;
import com.ecommerce.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Browse, search, and manage products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a product",
            description = "Creates a new product and an initial inventory row.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "SKU already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get a product by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.get(id);
    }

    @Operation(
            summary = "Search products",
            description = """
                    Returns a paginated list of products with optional filters and sort.

                    Examples:
                    - `?category=electronics&sort=price,asc`
                    - `?q=phone&minPrice=200&maxPrice=800&inStock=true`
                    - `?brand=acme&status=ACTIVE&sort=name,asc&page=0&size=20`
                    """)
    @GetMapping
    public PageResponse<ProductResponse> search(
            @Parameter(description = "Free-text query against name, description, and SKU")
            @RequestParam(required = false) String q,
            @Parameter(description = "Category slug, e.g. `electronics`")
            @RequestParam(required = false) String category,
            @Parameter(description = "Category numeric id (alternative to slug)")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Exact brand name (case-insensitive)")
            @RequestParam(required = false) String brand,
            @Parameter(description = "Product status filter")
            @RequestParam(required = false) ProductStatus status,
            @Parameter(description = "Minimum price (inclusive)")
            @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price (inclusive)")
            @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "If true, returns only products with available stock > 0")
            @RequestParam(required = false) Boolean inStock,
            @Parameter(description = "Pagination and sort, e.g. `page=0&size=20&sort=price,asc`")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {

        ProductSearchCriteria criteria = new ProductSearchCriteria(
                q, category, categoryId, brand, status, minPrice, maxPrice, inStock);
        Specification<Product> spec = ProductSpecifications.build(criteria);
        return PageResponse.from(productService.search(spec, pageable));
    }

    @Operation(summary = "Update a product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "SKU already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id,
                                  @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @Operation(summary = "Delete a product")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
