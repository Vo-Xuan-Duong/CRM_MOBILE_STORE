package com.example.Backend.dtos.stock;

import com.example.Backend.models.StockMovement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {

    private Long id;
    private Long skuId;
    private String skuName;
    private Long serialUnitId;
    private String serialNumber;
    private StockMovement.MovementType movementType;
    private Integer quantity;
    private StockMovement.MovementReason reason;
    private String refType;
    private Long refId;
    private String notes;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
}
