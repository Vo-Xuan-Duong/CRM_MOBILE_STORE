package com.example.Backend.dtos.model;

import com.example.Backend.models.Model;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ModelResponseDTO {

    private Long id;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private String name;
    private Integer releaseYear;
    private Model.OperatingSystem os;
    private String chipset;
    private Integer cpuCores;
    private BigDecimal cpuMaxGhz;
    private String gpu;

    // Display specifications
    private BigDecimal displaySizeInch;
    private String displayResolution;
    private Integer displayRefreshHz;
    private String displayPanel;
    private Integer displayBrightnessNits;
    private String glassProtection;

    // Camera specifications
    private Integer mainCameraMp;
    private Integer ultrawideCameraMp;
    private Integer telephotoMp;
    private Integer selfieCameraMp;
    private Boolean video4k;
    private Boolean video8k;

    // Battery specifications
    private Integer batteryCapacityMah;
    private Integer chargeWiredW;
    private Integer chargeWirelessW;
    private Integer reverseChargeW;

    // Connectivity specifications
    private String wifiVersion;
    private String bluetoothVersion;
    private Boolean nfc;
    private String usbType;
    private String gpsSystems;
    private Boolean cellular5g;
    private Boolean uwb;
    private Boolean irBlaster;

    // Additional specifications as JSON
    private Map<String, Object> specsJson;

    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
