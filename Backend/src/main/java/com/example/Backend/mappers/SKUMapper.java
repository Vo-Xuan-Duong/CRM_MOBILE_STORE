package com.example.Backend.mappers;

import com.example.Backend.dtos.product.SKUResponse;
import com.example.Backend.models.SKU;
import org.springframework.stereotype.Component;

@Component
public class SKUMapper {

    public SKUResponse toResponse(SKU sku) {
        if (sku == null) {
            return null;
        }

        return SKUResponse.builder()
                .id(sku.getId())
                .modelId(sku.getModel().getId())
                .modelName(sku.getModel().getName())
                .brandName(sku.getModel().getBrand().getName())
                .variantName(sku.getVariantName())
                .color(sku.getColor())
                .storageGb(sku.getStorageGb())
                .barcode(sku.getBarcode())
                .price(sku.getPrice())
                .costPrice(sku.getCostPrice())
                .isSerialized(sku.getIsSerialized())
                .isActive(sku.getIsActive())
                .createdAt(sku.getCreatedAt())
                .updatedAt(sku.getUpdatedAt())
                .build();
    }
}
