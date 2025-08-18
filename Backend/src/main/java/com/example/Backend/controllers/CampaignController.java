package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.campaign.CampaignRequest;
import com.example.Backend.dtos.campaign.CampaignResponse;
import com.example.Backend.dtos.campaign.CampaignTargetRequest;
import com.example.Backend.services.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
@Tag(name = "Campaign", description = "Marketing Campaign Management API")
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    @PreAuthorize("hasAuthority('CAMPAIGN_CREATE')")
    @Operation(summary = "Create new campaign")
    public ResponseEntity<ResponseData<CampaignResponse>> createCampaign(
            @Valid @RequestBody CampaignRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        CampaignResponse response = campaignService.createCampaign(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<CampaignResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Campaign created successfully")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CAMPAIGN_UPDATE')")
    @Operation(summary = "Update campaign")
    public ResponseEntity<ResponseData<CampaignResponse>> updateCampaign(
            @PathVariable Long id,
            @Valid @RequestBody CampaignRequest request) {
        CampaignResponse response = campaignService.updateCampaign(id, request);
        return ResponseEntity.ok(ResponseData.<CampaignResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Campaign updated successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @Operation(summary = "Get campaign by ID")
    public ResponseEntity<ResponseData<CampaignResponse>> getCampaignById(@PathVariable Long id) {
        CampaignResponse response = campaignService.getCampaignById(id);
        return ResponseEntity.ok(ResponseData.<CampaignResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Campaign retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @Operation(summary = "Get all campaigns with pagination")
    public ResponseEntity<ResponseData<Page<CampaignResponse>>> getAllCampaigns(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CampaignResponse> response = campaignService.getAllCampaigns(pageable);
        return ResponseEntity.ok(ResponseData.<Page<CampaignResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Campaigns retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @Operation(summary = "Get campaigns by status")
    public ResponseEntity<ResponseData<List<CampaignResponse>>> getCampaignsByStatus(
            @PathVariable String status) {
        List<CampaignResponse> response = campaignService.getCampaignsByStatus(status);
        return ResponseEntity.ok(ResponseData.<List<CampaignResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Campaigns by status retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @Operation(summary = "Get campaigns by type")
    public ResponseEntity<ResponseData<List<CampaignResponse>>> getCampaignsByType(
            @PathVariable String type) {
        List<CampaignResponse> response = campaignService.getCampaignsByType(type);
        return ResponseEntity.ok(ResponseData.<List<CampaignResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Campaigns by type retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @Operation(summary = "Get active campaigns")
    public ResponseEntity<ResponseData<List<CampaignResponse>>> getActiveCampaigns() {
        List<CampaignResponse> response = campaignService.getActiveCampaigns();
        return ResponseEntity.ok(ResponseData.<List<CampaignResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Active campaigns retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasAuthority('CAMPAIGN_UPDATE')")
    @Operation(summary = "Start campaign")
    public ResponseEntity<ResponseData<Void>> startCampaign(@PathVariable Long id) {
        campaignService.startCampaign(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Campaign started successfully")
                .build());
    }

    @PutMapping("/{id}/pause")
    @PreAuthorize("hasAuthority('CAMPAIGN_UPDATE')")
    @Operation(summary = "Pause campaign")
    public ResponseEntity<ResponseData<Void>> pauseCampaign(@PathVariable Long id) {
        campaignService.pauseCampaign(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Campaign paused successfully")
                .build());
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('CAMPAIGN_UPDATE')")
    @Operation(summary = "Complete campaign")
    public ResponseEntity<ResponseData<Void>> completeCampaign(@PathVariable Long id) {
        campaignService.completeCampaign(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Campaign completed successfully")
                .build());
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('CAMPAIGN_UPDATE')")
    @Operation(summary = "Cancel campaign")
    public ResponseEntity<ResponseData<Void>> cancelCampaign(@PathVariable Long id) {
        campaignService.cancelCampaign(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Campaign cancelled successfully")
                .build());
    }

    @PostMapping("/{id}/targets")
    @PreAuthorize("hasAuthority('CAMPAIGN_UPDATE')")
    @Operation(summary = "Add targets to campaign")
    public ResponseEntity<ResponseData<Void>> addTargets(
            @PathVariable Long id,
            @Valid @RequestBody CampaignTargetRequest request) {
        campaignService.addTargets(id, request.getCustomerIds());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<Void>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Targets added to campaign successfully")
                        .build());
    }

    @DeleteMapping("/{id}/targets/{customerId}")
    @PreAuthorize("hasAuthority('CAMPAIGN_UPDATE')")
    @Operation(summary = "Remove target from campaign")
    public ResponseEntity<ResponseData<Void>> removeTarget(
            @PathVariable Long id,
            @PathVariable Long customerId) {
        campaignService.removeTarget(id, customerId);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Target removed from campaign successfully")
                .build());
    }

    @GetMapping("/{id}/targets")
    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @Operation(summary = "Get campaign targets")
    public ResponseEntity<ResponseData<List<Object>>> getCampaignTargets(@PathVariable Long id) {
        List<Object> targets = campaignService.getCampaignTargets(id);
        return ResponseEntity.ok(ResponseData.<List<Object>>builder()
                .status(HttpStatus.OK.value())
                .message("Campaign targets retrieved successfully")
                .data(targets)
                .build());
    }

    @GetMapping("/{id}/performance")
    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @Operation(summary = "Get campaign performance")
    public ResponseEntity<ResponseData<CampaignPerformance>> getCampaignPerformance(@PathVariable Long id) {
        CampaignPerformance performance = campaignService.getCampaignPerformance(id);
        return ResponseEntity.ok(ResponseData.<CampaignPerformance>builder()
                .status(HttpStatus.OK.value())
                .message("Campaign performance retrieved successfully")
                .data(performance)
                .build());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @Operation(summary = "Search campaigns")
    public ResponseEntity<ResponseData<Page<CampaignResponse>>> searchCampaigns(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CampaignResponse> response = campaignService.searchCampaigns(keyword, pageable);
        return ResponseEntity.ok(ResponseData.<Page<CampaignResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Campaigns searched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get campaign statistics")
    public ResponseEntity<ResponseData<CampaignStatistics>> getCampaignStatistics() {
        CampaignStatistics stats = campaignService.getCampaignStatistics();
        return ResponseEntity.ok(ResponseData.<CampaignStatistics>builder()
                .status(HttpStatus.OK.value())
                .message("Campaign statistics retrieved successfully")
                .data(stats)
                .build());
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            // Assuming username is the user ID or you have a way to get user ID
            // This is a simplified version - in real implementation, you'd get the actual user ID
            return 1L; // Replace with actual logic to get user ID from UserDetails
        }
        throw new RuntimeException("Unable to get user ID from authentication");
    }

    @lombok.Data
    @lombok.Builder
    public static class CampaignPerformance {
        private Long campaignId;
        private String campaignName;
        private Integer totalTargets;
        private Integer reachedTargets;
        private Double reachRate;
        private Integer conversions;
        private Double conversionRate;
        private java.math.BigDecimal totalRevenue;
        private java.math.BigDecimal roi;
    }

    @lombok.Data
    @lombok.Builder
    public static class CampaignStatistics {
        private long totalCampaigns;
        private long activeCampaigns;
        private long completedCampaigns;
        private long cancelledCampaigns;
        private java.math.BigDecimal totalBudget;
        private java.math.BigDecimal totalSpent;
        private Double averageReachRate;
        private Double averageConversionRate;
    }
}
