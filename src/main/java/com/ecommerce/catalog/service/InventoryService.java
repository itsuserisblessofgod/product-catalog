package com.ecommerce.catalog.service;

import com.ecommerce.catalog.domain.Inventory;
import com.ecommerce.catalog.domain.Product;
import com.ecommerce.catalog.domain.ProductStatus;
import com.ecommerce.catalog.dto.InventoryRequest;
import com.ecommerce.catalog.dto.InventoryResponse;
import com.ecommerce.catalog.exception.ResourceNotFoundException;
import com.ecommerce.catalog.mapper.InventoryMapper;
import com.ecommerce.catalog.repository.InventoryRepository;
import com.ecommerce.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository,
                            InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @Transactional(readOnly = true)
    public InventoryResponse getByProductId(Long productId) {
        return inventoryMapper.toResponse(getOrThrow(productId));
    }

    public InventoryResponse upsert(Long productId, InventoryRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        Inventory inv = inventoryRepository.findByProductId(productId).orElseGet(() -> {
            Inventory created = Inventory.builder().product(product).build();
            product.setInventory(created);
            return created;
        });

        inv.setQuantity(request.quantity());
        if (request.reservedQuantity() != null) {
            inv.setReservedQuantity(request.reservedQuantity());
        }
        if (request.lowStockThreshold() != null) {
            inv.setLowStockThreshold(request.lowStockThreshold());
        }
        if (request.warehouseLocation() != null) {
            inv.setWarehouseLocation(request.warehouseLocation());
        }

        syncProductStatus(product, inv);
        Inventory saved = inventoryRepository.save(inv);
        return inventoryMapper.toResponse(saved);
    }

    public InventoryResponse adjustStock(Long productId, int delta) {
        Inventory inv = getOrThrow(productId);
        int newQty = inv.getQuantity() + delta;
        if (newQty < 0) {
            throw new IllegalArgumentException(
                    "Insufficient stock: have " + inv.getQuantity() + ", delta " + delta);
        }
        inv.setQuantity(newQty);
        syncProductStatus(inv.getProduct(), inv);
        return inventoryMapper.toResponse(inv);
    }

    private Inventory getOrThrow(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product: " + productId));
    }

    private void syncProductStatus(Product product, Inventory inv) {
        if (product.getStatus() == ProductStatus.DRAFT
                || product.getStatus() == ProductStatus.DISCONTINUED) {
            return;
        }
        product.setStatus(inv.availableQuantity() > 0
                ? ProductStatus.ACTIVE
                : ProductStatus.OUT_OF_STOCK);
    }
}
