package com.example.Backend.dtos.serial;

import jakarta.validation.constraints.NotBlank;
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
public class SerialUnitRequest {

    @NotNull(message = "SKU ID is required")
    private Long skuId;

    @NotBlank(message = "IMEI is required")
    private String imei;

    private String serialNumber;

    private LocalDate purchaseDate;

    private String notes;
}
