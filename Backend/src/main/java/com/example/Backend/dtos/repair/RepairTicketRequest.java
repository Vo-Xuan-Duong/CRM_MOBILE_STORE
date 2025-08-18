package com.example.Backend.dtos.repair;

import com.example.Backend.models.RepairTicket;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairTicketRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Long serialUnitId;

    @NotBlank(message = "Device info is required")
    private String deviceInfo;

    @NotBlank(message = "Issue description is required")
    private String issueDescription;

    @NotNull(message = "Priority is required")
    private RepairTicket.RepairPriority priority;

    @DecimalMin(value = "0.0", inclusive = true, message = "Estimated cost must be non-negative")
    private BigDecimal estimatedCost = BigDecimal.ZERO;
}
