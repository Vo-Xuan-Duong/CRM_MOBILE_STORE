package com.example.Backend.dtos.model;

import com.example.Backend.models.Model;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ModelCreateDTO {

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    private Long categoryId;

    @NotBlank(message = "Model name is required")
    @Size(max = 255, message = "Model name must not exceed 255 characters")
    private String name;

    @Min(value = 2000, message = "Release year must be at least 2000")
    @Max(value = 2030, message = "Release year must not exceed 2030")
    private Integer releaseYear;

    private Model.OperatingSystem os;

    @Size(max = 100, message = "Chipset name must not exceed 100 characters")
    private String chipset;

    @Min(value = 1, message = "CPU cores must be at least 1")
    @Max(value = 32, message = "CPU cores must not exceed 32")
    private Integer cpuCores;

    @DecimalMin(value = "0.1", message = "CPU max GHz must be at least 0.1")
    @DecimalMax(value = "10.0", message = "CPU max GHz must not exceed 10.0")
    private BigDecimal cpuMaxGhz;

    @Size(max = 100, message = "GPU name must not exceed 100 characters")
    private String gpu;

    // Display specifications
    @DecimalMin(value = "1.0", message = "Display size must be at least 1.0 inch")
    @DecimalMax(value = "20.0", message = "Display size must not exceed 20.0 inches")
    private BigDecimal displaySizeInch;

    @Size(max = 32, message = "Display resolution must not exceed 32 characters")
    private String displayResolution;

    @Min(value = 30, message = "Refresh rate must be at least 30Hz")
    @Max(value = 240, message = "Refresh rate must not exceed 240Hz")
    private Integer displayRefreshHz;

    @Size(max = 24, message = "Display panel must not exceed 24 characters")
    private String displayPanel;

    @Min(value = 100, message = "Brightness must be at least 100 nits")
    @Max(value = 10000, message = "Brightness must not exceed 10000 nits")
    private Integer displayBrightnessNits;

    @Size(max = 64, message = "Glass protection must not exceed 64 characters")
    private String glassProtection;

    // Camera specifications
    @Min(value = 1, message = "Main camera MP must be at least 1")
    @Max(value = 200, message = "Main camera MP must not exceed 200")
    private Integer mainCameraMp;

    @Min(value = 1, message = "Ultrawide camera MP must be at least 1")
    @Max(value = 200, message = "Ultrawide camera MP must not exceed 200")
    private Integer ultrawideCameraMp;

    @Min(value = 1, message = "Telephoto camera MP must be at least 1")
    @Max(value = 200, message = "Telephoto camera MP must not exceed 200")
    private Integer telephotoMp;

    @Min(value = 1, message = "Selfie camera MP must be at least 1")
    @Max(value = 100, message = "Selfie camera MP must not exceed 100")
    private Integer selfieCameraMp;

    private Boolean video4k;
    private Boolean video8k;

    // Battery specifications
    @Min(value = 500, message = "Battery capacity must be at least 500 mAh")
    @Max(value = 20000, message = "Battery capacity must not exceed 20000 mAh")
    private Integer batteryCapacityMah;

    @Min(value = 5, message = "Wired charging must be at least 5W")
    @Max(value = 500, message = "Wired charging must not exceed 500W")
    private Integer chargeWiredW;

    @Min(value = 5, message = "Wireless charging must be at least 5W")
    @Max(value = 100, message = "Wireless charging must not exceed 100W")
    private Integer chargeWirelessW;

    @Min(value = 1, message = "Reverse charging must be at least 1W")
    @Max(value = 50, message = "Reverse charging must not exceed 50W")
    private Integer reverseChargeW;

    // Connectivity specifications
    @Size(max = 16, message = "WiFi version must not exceed 16 characters")
    private String wifiVersion;

    @Size(max = 16, message = "Bluetooth version must not exceed 16 characters")
    private String bluetoothVersion;

    private Boolean nfc;

    @Size(max = 16, message = "USB type must not exceed 16 characters")
    private String usbType;

    @Size(max = 100, message = "GPS systems must not exceed 100 characters")
    private String gpsSystems;

    private Boolean cellular5g;
    private Boolean uwb;
    private Boolean irBlaster;

    // Additional specifications as JSON
    private Map<String, Object> specsJson;

    private Boolean isActive = true;
}
