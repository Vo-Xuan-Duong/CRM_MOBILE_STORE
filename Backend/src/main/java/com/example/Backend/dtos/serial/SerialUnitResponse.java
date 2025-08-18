package com.example.Backend.dtos.serial;

import com.example.Backend.models.SerialUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerialUnitResponse {

    private Long id;
    private Long skuId;
    private String skuName;
    private String brandName;
    private String imei;
    private String serialNumber;
    private SerialUnit.SerialStatus status;
    private LocalDate purchaseDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
