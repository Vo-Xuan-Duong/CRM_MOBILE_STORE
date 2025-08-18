package com.example.Backend.mappers;

import com.example.Backend.dtos.stock.StockMovementResponse;
import com.example.Backend.models.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

    public StockMovementResponse toResponse(StockMovement stockMovement) {
        if (stockMovement == null) {
            return null;
        }

        return StockMovementResponse.builder()
                .id(stockMovement.getId())
                .skuId(stockMovement.getSku().getId())
                .skuName(stockMovement.getSku().getModel().getName())
                .serialUnitId(stockMovement.getSerialUnit() != null ? stockMovement.getSerialUnit().getId() : null)
                .serialNumber(stockMovement.getSerialUnit() != null ? stockMovement.getSerialUnit().getImei() : null)
                .movementType(stockMovement.getMovementType())
                .quantity(stockMovement.getQuantity())
                .reason(stockMovement.getReason())
                .refType(stockMovement.getRefType())
                .refId(stockMovement.getRefId())
                .notes(stockMovement.getNotes())
                .createdById(stockMovement.getCreatedBy() != null ? stockMovement.getCreatedBy().getId() : null)
                .createdByName(stockMovement.getCreatedBy() != null ? stockMovement.getCreatedBy().getFullName() : null)
                .createdAt(stockMovement.getCreatedAt())
                .build();
    }
}
