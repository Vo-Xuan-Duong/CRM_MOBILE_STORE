package com.example.Backend.dtos.warranty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Order item ID is required")
    private Long orderItemId;

    private Long serialUnitId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Warranty months is required")
    private Integer months;

    private String notes;
}
