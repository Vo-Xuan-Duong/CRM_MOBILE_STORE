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

    // ==================== STATUS MANAGEMENT APIs ====================

    @PutMapping("/{id}/close")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE')")
    @Operation(summary = "Close interaction")
    public ResponseEntity<ResponseData<Void>> closeInteraction(@PathVariable Long id) {
        interactionService.closeInteraction(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Interaction closed successfully")
                .build());
    }

    @PutMapping("/{id}/reopen")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE')")
    @Operation(summary = "Reopen interaction")
    public ResponseEntity<ResponseData<Void>> reopenInteraction(@PathVariable Long id) {
        interactionService.reopenInteraction(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Interaction reopened successfully")
                .build());
    }

    @PutMapping("/{id}/in-progress")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE')")
    @Operation(summary = "Mark interaction as in progress")
    public ResponseEntity<ResponseData<Void>> markAsInProgress(@PathVariable Long id) {
        interactionService.markAsInProgress(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Interaction marked as in progress successfully")
                .build());
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        // Extract user ID from authentication token or principal
        // For now, returning a placeholder - this should be implemented based on your security setup
        if (authentication != null && authentication.getPrincipal() != null) {
            // Implementation depends on your security configuration
            // Example: return ((UserPrincipal) authentication.getPrincipal()).getId();
            // You can implement this based on your UserDetailsService or JWT token structure
        }
        return 1L; // Placeholder - should be replaced with actual user ID extraction
    }
}
