package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.repair.RepairTicketRequest;
import com.example.Backend.dtos.repair.RepairTicketResponse;
import com.example.Backend.services.RepairTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/repair-tickets")
@RequiredArgsConstructor
@Tag(name = "Repair Ticket", description = "Repair Ticket Management API")
public class RepairTicketController {

    private final RepairTicketService repairTicketService;

    @PostMapping
    @PreAuthorize("hasAuthority('REPAIR_CREATE')")
    @Operation(summary = "Create new repair ticket")
    public ResponseEntity<ResponseData<RepairTicketResponse>> createRepairTicket(
            @Valid @RequestBody RepairTicketRequest request) {
        RepairTicketResponse response = repairTicketService.createRepairTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<RepairTicketResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Repair ticket created successfully")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('REPAIR_UPDATE')")
    @Operation(summary = "Update repair ticket")
    public ResponseEntity<ResponseData<RepairTicketResponse>> updateRepairTicket(
            @PathVariable Long id,
            @Valid @RequestBody RepairTicketRequest request) {
        RepairTicketResponse response = repairTicketService.updateRepairTicket(id, request);
        return ResponseEntity.ok(ResponseData.<RepairTicketResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Repair ticket updated successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('REPAIR_READ')")
    @Operation(summary = "Get repair ticket by ID")
    public ResponseEntity<ResponseData<RepairTicketResponse>> getRepairTicketById(@PathVariable Long id) {
        RepairTicketResponse response = repairTicketService.getRepairTicketById(id);
        return ResponseEntity.ok(ResponseData.<RepairTicketResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Repair ticket retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/ticket-number/{ticketNumber}")
    @PreAuthorize("hasAuthority('REPAIR_READ')")
    @Operation(summary = "Get repair ticket by ticket number")
    public ResponseEntity<ResponseData<RepairTicketResponse>> getRepairTicketByNumber(@PathVariable String ticketNumber) {
        RepairTicketResponse response = repairTicketService.getRepairTicketByTicketNumber(ticketNumber);
        return ResponseEntity.ok(ResponseData.<RepairTicketResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Repair ticket retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REPAIR_READ')")
    @Operation(summary = "Get all repair tickets with pagination")
    public ResponseEntity<ResponseData<Page<RepairTicketResponse>>> getAllRepairTickets(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RepairTicketResponse> response = repairTicketService.getAllRepairTickets(pageable);
        return ResponseEntity.ok(ResponseData.<Page<RepairTicketResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Repair tickets retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('REPAIR_READ')")
    @Operation(summary = "Get repair tickets by customer")
    public ResponseEntity<ResponseData<List<RepairTicketResponse>>> getRepairTicketsByCustomer(
            @PathVariable Long customerId) {
        List<RepairTicketResponse> response = repairTicketService.getRepairTicketsByCustomer(customerId);
        return ResponseEntity.ok(ResponseData.<List<RepairTicketResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Repair tickets by customer retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('REPAIR_READ')")
    @Operation(summary = "Get repair tickets by status")
    public ResponseEntity<ResponseData<List<RepairTicketResponse>>> getRepairTicketsByStatus(
            @PathVariable String status) {
        List<RepairTicketResponse> response = repairTicketService.getRepairTicketsByStatus(status);
        return ResponseEntity.ok(ResponseData.<List<RepairTicketResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Repair tickets by status retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasAuthority('REPAIR_READ')")
    @Operation(summary = "Get repair tickets by technician")
    public ResponseEntity<ResponseData<List<RepairTicketResponse>>> getRepairTicketsByTechnician(
            @PathVariable Long technicianId) {
        List<RepairTicketResponse> response = repairTicketService.getRepairTicketsByTechnician(technicianId);
        return ResponseEntity.ok(ResponseData.<List<RepairTicketResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Repair tickets by technician retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}/start-repair")
    @PreAuthorize("hasAuthority('REPAIR_UPDATE')")
    @Operation(summary = "Start repair with technician assignment")
    public ResponseEntity<ResponseData<Void>> startRepair(
            @PathVariable Long id,
            @RequestParam Long technicianId) {
        repairTicketService.startRepair(id, technicianId); // Use existing startRepair method
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Repair started and technician assigned successfully")
                .build());
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('REPAIR_UPDATE')")
    @Operation(summary = "Complete repair ticket")
    public ResponseEntity<ResponseData<Void>> completeRepair(
            @PathVariable Long id,
            @RequestParam BigDecimal actualCost,
            @RequestParam String diagnosis) {
        repairTicketService.completeRepair(id, actualCost, diagnosis); // Use correct method signature
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Repair ticket completed successfully")
                .build());
    }

    @PutMapping("/{id}/deliver")
    @PreAuthorize("hasAuthority('REPAIR_UPDATE')")
    @Operation(summary = "Mark repair as delivered")
    public ResponseEntity<ResponseData<Void>> deliverDevice(@PathVariable Long id) {
        repairTicketService.deliverDevice(id); // Use existing deliverDevice method
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Device delivered successfully")
                .build());
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('REPAIR_UPDATE')")
    @Operation(summary = "Cancel repair ticket")
    public ResponseEntity<ResponseData<Void>> cancelRepair(
            @PathVariable Long id,
            @RequestParam String cancellationReason) {
        repairTicketService.cancelRepair(id, cancellationReason); // Use existing cancelRepair method
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Repair ticket cancelled successfully")
                .build());
    }
}
