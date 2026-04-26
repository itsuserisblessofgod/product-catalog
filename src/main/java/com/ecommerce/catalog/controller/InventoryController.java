package com.ecommerce.catalog.controller;

import com.ecommerce.catalog.dto.InventoryRequest;
import com.ecommerce.catalog.dto.InventoryResponse;
import com.ecommerce.catalog.service.InventoryService;
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
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public InventoryResponse get(@PathVariable Long productId) {
        return inventoryService.getByProductId(productId);
    }

    @PutMapping
    public InventoryResponse upsert(@PathVariable Long productId,
                                    @Valid @RequestBody InventoryRequest request) {
        return inventoryService.upsert(productId, request);
    }

    @PostMapping("/adjust")
    public InventoryResponse adjust(@PathVariable Long productId,
                                    @RequestParam @NotNull Integer delta) {
        return inventoryService.adjustStock(productId, delta);
    }
}
