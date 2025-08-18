package com.example.Backend.dtos.model;

import com.example.Backend.models.Model;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ModelSearchRequest {

    private String keyword; // Search in name, chipset, gpu
    private List<Long> brandIds;
    private List<Long> categoryIds;
    private List<Model.OperatingSystem> operatingSystems;

    // Release year range
    private Integer releaseYearFrom;
    private Integer releaseYearTo;

    // Display specifications filters
    private BigDecimal displaySizeFrom;
    private BigDecimal displaySizeTo;
    private Integer refreshHzFrom;
    private Integer refreshHzTo;
    private List<String> displayPanels;

    // Camera specifications filters
    private Integer mainCameraMpFrom;
    private Integer mainCameraMpTo;
    private Boolean hasUltrawide;
    private Boolean hasTelephoto;
    private Boolean supportsVideo4k;
    private Boolean supportsVideo8k;

    // Battery specifications filters
    private Integer batteryCapacityFrom;
    private Integer batteryCapacityTo;
    private Integer chargeWiredFrom;
    private Integer chargeWiredTo;
    private Boolean hasWirelessCharging;
    private Boolean hasReverseCharging;

    // Connectivity filters
    private List<String> wifiVersions;
    private List<String> bluetoothVersions;
    private Boolean hasNfc;
    private Boolean has5g;
    private Boolean hasUwb;
    private List<String> usbTypes;

    // Status filter
    private Boolean isActive;

    // Sorting
    private String sortBy = "name"; // name, releaseYear, displaySize, batteryCapacity, createdAt
    private String sortDirection = "ASC"; // ASC, DESC

    // Pagination
    private Integer page = 0;
    private Integer size = 20;
}
