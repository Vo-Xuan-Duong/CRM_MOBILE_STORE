package com.example.Backend.mappers;

import com.example.Backend.dtos.stock.StockItemResponse;
import com.example.Backend.models.StockItem;
import org.springframework.stereotype.Component;

@Component
public class StockItemMapper {

    public StockItemResponse toResponse(StockItem stockItem) {
        if (stockItem == null) {
            return null;
        }

        return StockItemResponse.builder()
                .id(stockItem.getId())
                .skuId(stockItem.getSku().getId())
                .skuName(stockItem.getSku().getModel().getName() + " " +
                        (stockItem.getSku().getVariantName() != null ? stockItem.getSku().getVariantName() : ""))
                .brandName(stockItem.getSku().getModel().getBrand().getName())
                .quantity(stockItem.getQuantity())
                .reservedQty(stockItem.getReservedQty())
                .availableQty(stockItem.getAvailableQuantity())
                .minStock(stockItem.getMinStock())
                .maxStock(stockItem.getMaxStock())
                .isLowStock(stockItem.isLowStock())
                .updatedAt(stockItem.getUpdatedAt())
                .build();
    }
}
