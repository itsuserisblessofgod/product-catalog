package com.ecommerce.catalog.controller;

import com.ecommerce.catalog.dto.ApiError;
import com.ecommerce.catalog.dto.InventoryRequest;
import com.ecommerce.catalog.dto.InventoryResponse;
import com.ecommerce.catalog.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/{productId}/inventory")
@Tag(name = "Inventory", description = "Inspect and adjust product stock levels")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "Get inventory for a product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory found"),
            @ApiResponse(responseCode = "404", description = "Product or inventory not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public InventoryResponse get(@PathVariable Long productId) {
        return inventoryService.getByProductId(productId);
    }

    @Operation(summary = "Replace inventory for a product",
            description = "Sets quantity, reserved, threshold, and warehouse location. "
                    + "Auto-syncs product status between ACTIVE and OUT_OF_STOCK.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory updated"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Concurrent modification",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping
    public InventoryResponse upsert(@PathVariable Long productId,
                                    @Valid @RequestBody InventoryRequest request) {
        return inventoryService.upsert(productId, request);
    }

    @Operation(summary = "Adjust stock by a delta",
            description = "Positive `delta` increments stock, negative decrements. "
                    + "Rejects negative resulting quantity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock adjusted"),
            @ApiResponse(responseCode = "400", description = "Insufficient stock",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Inventory not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/adjust")
    public InventoryResponse adjust(@PathVariable Long productId,
                                    @Parameter(description = "Signed change to apply", example = "-1")
                                    @RequestParam @NotNull Integer delta) {
        return inventoryService.adjustStock(productId, delta);
    }
}
