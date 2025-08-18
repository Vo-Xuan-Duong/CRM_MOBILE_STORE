package com.example.Backend.dtos.stock;

import com.example.Backend.models.StockMovement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementRequest {

    @NotNull(message = "SKU ID is required")
    private Long skuId;

    private Long serialUnitId;

    @Min(value = 1, message = "Quantity must be positive")
    private Integer quantity = 1;

    @NotNull(message = "Reason is required")
    private StockMovement.MovementReason reason;

    private String refType;

    private Long refId;

    private String notes;

    private Integer minStock;

    private Integer maxStock;
}
