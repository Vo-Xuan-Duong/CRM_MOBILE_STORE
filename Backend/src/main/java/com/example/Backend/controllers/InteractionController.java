package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.interaction.InteractionRequest;
import com.example.Backend.dtos.interaction.InteractionResponse;
import com.example.Backend.services.InteractionService;
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
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/interactions")
@RequiredArgsConstructor
@Tag(name = "Interaction", description = "Customer Interaction Management API")
public class InteractionController {

    private final InteractionService interactionService;

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE')")
    @Operation(summary = "Create new interaction")
    public ResponseEntity<ResponseData<InteractionResponse>> createInteraction(
            @Valid @RequestBody InteractionRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        InteractionResponse response = interactionService.createInteraction(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<InteractionResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Interaction created successfully")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE')")
    @Operation(summary = "Update interaction")
    public ResponseEntity<ResponseData<InteractionResponse>> updateInteraction(
            @PathVariable Long id,
            @Valid @RequestBody InteractionRequest request) {
        InteractionResponse response = interactionService.updateInteraction(id, request);
        return ResponseEntity.ok(ResponseData.<InteractionResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Interaction updated successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    @Operation(summary = "Get interaction by ID")
    public ResponseEntity<ResponseData<InteractionResponse>> getInteractionById(@PathVariable Long id) {
        InteractionResponse response = interactionService.getInteractionById(id);
        return ResponseEntity.ok(ResponseData.<InteractionResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Interaction retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    @Operation(summary = "Get all interactions with pagination")
    public ResponseEntity<ResponseData<Page<InteractionResponse>>> getAllInteractions(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<InteractionResponse> response = interactionService.getAllInteractions(pageable);
        return ResponseEntity.ok(ResponseData.<Page<InteractionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Interactions retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    @Operation(summary = "Get interactions by customer")
    public ResponseEntity<ResponseData<List<InteractionResponse>>> getInteractionsByCustomer(
            @PathVariable Long customerId) {
        List<InteractionResponse> response = interactionService.getInteractionsByCustomer(customerId);
        return ResponseEntity.ok(ResponseData.<List<InteractionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Interactions by customer retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    @Operation(summary = "Get interactions by user")
    public ResponseEntity<ResponseData<List<InteractionResponse>>> getInteractionsByUser(
            @PathVariable Long userId) {
        List<InteractionResponse> response = interactionService.getInteractionsByUser(userId);
        return ResponseEntity.ok(ResponseData.<List<InteractionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Interactions by user retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    @Operation(summary = "Get interactions by type")
    public ResponseEntity<ResponseData<List<InteractionResponse>>> getInteractionsByType(
            @PathVariable String type) {
        List<InteractionResponse> response = interactionService.getInteractionsByType(type);
        return ResponseEntity.ok(ResponseData.<List<InteractionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Interactions by type retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    @Operation(summary = "Get interactions by date range")
    public ResponseEntity<ResponseData<List<InteractionResponse>>> getInteractionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<InteractionResponse> response = interactionService.getInteractionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ResponseData.<List<InteractionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Interactions by date range retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/follow-ups")
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    @Operation(summary = "Get interactions requiring follow-up")
    public ResponseEntity<ResponseData<List<InteractionResponse>>> getInteractionsRequiringFollowUp() {
        List<InteractionResponse> response = interactionService.getInteractionsRequiringFollowUp();
        return ResponseEntity.ok(ResponseData.<List<InteractionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Follow-up interactions retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/overdue-follow-ups")
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    @Operation(summary = "Get overdue follow-up interactions")
    public ResponseEntity<ResponseData<List<InteractionResponse>>> getOverdueFollowUps() {
        List<InteractionResponse> response = interactionService.getOverdueFollowUps();
        return ResponseEntity.ok(ResponseData.<List<InteractionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Overdue follow-up interactions retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}/complete-follow-up")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE')")
    @Operation(summary = "Mark follow-up as completed")
    public ResponseEntity<ResponseData<Void>> completeFollowUp(
            @PathVariable Long id,
            @RequestParam(required = false) String outcome) {
        interactionService.completeFollowUp(id, outcome);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Follow-up completed successfully")
                .build());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    @Operation(summary = "Search interactions")
    public ResponseEntity<ResponseData<Page<InteractionResponse>>> searchInteractions(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<InteractionResponse> response = interactionService.searchInteractions(keyword, pageable);
        return ResponseEntity.ok(ResponseData.<Page<InteractionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Interactions searched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get interaction statistics")
    public ResponseEntity<ResponseData<InteractionStatistics>> getInteractionStatistics() {
        InteractionStatistics stats = interactionService.getInteractionStatistics();
        return ResponseEntity.ok(ResponseData.<InteractionStatistics>builder()
                .status(HttpStatus.OK.value())
                .message("Interaction statistics retrieved successfully")
                .data(stats)
                .build());
    }

    @GetMapping("/statistics/by-type")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get interaction statistics by type")
    public ResponseEntity<ResponseData<List<Object[]>>> getInteractionStatsByType() {
        List<Object[]> stats = interactionService.getInteractionStatsByType();
        return ResponseEntity.ok(ResponseData.<List<Object[]>>builder()
                .status(HttpStatus.OK.value())
                .message("Interaction statistics by type retrieved successfully")
                .data(stats)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_DELETE')")
    @Operation(summary = "Delete interaction")
    public ResponseEntity<ResponseData<Void>> deleteInteraction(@PathVariable Long id) {
        interactionService.deleteInteraction(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Interaction deleted successfully")
                .build());
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        // Implementation to extract user ID from authentication
        return 1L; // Placeholder
    }

    @lombok.Data
    @lombok.Builder
    public static class InteractionStatistics {
        private long totalInteractions;
        private long callsCount;
        private long emailsCount;
        private long smsCount;
        private long visitsCount;
        private long complaintsCount;
        private long pendingFollowUps;
        private long overdueFollowUps;
    }
}
