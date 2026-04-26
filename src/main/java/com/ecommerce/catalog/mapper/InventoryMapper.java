package com.ecommerce.catalog.mapper;

import com.ecommerce.catalog.domain.Inventory;
import com.ecommerce.catalog.dto.InventoryResponse;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory inv) {
        int available = inv.availableQuantity();
        return new InventoryResponse(
                inv.getId(),
                inv.getProduct().getId(),
                inv.getProduct().getSku(),
                inv.getQuantity(),
                inv.getReservedQuantity(),
                available,
                inv.getLowStockThreshold(),
                available <= inv.getLowStockThreshold(),
                inv.getWarehouseLocation(),
                inv.getUpdatedAt()
        );
    }
}
