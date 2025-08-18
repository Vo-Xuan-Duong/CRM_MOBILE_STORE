package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.invoice.InvoiceRequest;
import com.example.Backend.dtos.invoice.InvoiceResponse;
import com.example.Backend.services.InvoiceService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice", description = "Invoice Management API")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasAuthority('SALES_CREATE')")
    @Operation(summary = "Create new invoice")
    public ResponseEntity<ResponseData<InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        InvoiceResponse response = invoiceService.createInvoice(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<InvoiceResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Invoice created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ResponseData<InvoiceResponse>> getInvoiceById(@PathVariable Long id) {
        InvoiceResponse response = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ResponseData.<InvoiceResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Invoice retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/number/{invoiceNo}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get invoice by invoice number")
    public ResponseEntity<ResponseData<InvoiceResponse>> getInvoiceByNumber(@PathVariable String invoiceNo) {
        InvoiceResponse response = invoiceService.getInvoiceByNumber(invoiceNo);
        return ResponseEntity.ok(ResponseData.<InvoiceResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Invoice retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get invoice by order ID")
    public ResponseEntity<ResponseData<InvoiceResponse>> getInvoiceByOrderId(@PathVariable Long orderId) {
        InvoiceResponse response = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(ResponseData.<InvoiceResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Invoice by order retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get all invoices with pagination")
    public ResponseEntity<ResponseData<Page<InvoiceResponse>>> getAllInvoices(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<InvoiceResponse> response = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(ResponseData.<Page<InvoiceResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Invoices retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get invoices by status")
    public ResponseEntity<ResponseData<List<InvoiceResponse>>> getInvoicesByStatus(
            @PathVariable String status) {
        List<InvoiceResponse> response = invoiceService.getInvoicesByStatus(status);
        return ResponseEntity.ok(ResponseData.<List<InvoiceResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Invoices by status retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get invoices by type")
    public ResponseEntity<ResponseData<List<InvoiceResponse>>> getInvoicesByType(
            @PathVariable String type) {
        List<InvoiceResponse> response = invoiceService.getInvoicesByType(type);
        return ResponseEntity.ok(ResponseData.<List<InvoiceResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Invoices by type retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}/issue")
    @PreAuthorize("hasAuthority('SALES_UPDATE')")
    @Operation(summary = "Issue invoice")
    public ResponseEntity<ResponseData<Void>> issueInvoice(@PathVariable Long id) {
        invoiceService.issueInvoice(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Invoice issued successfully")
                .build());
    }

    @PutMapping("/{id}/send")
    @PreAuthorize("hasAuthority('SALES_UPDATE')")
    @Operation(summary = "Send invoice")
    public ResponseEntity<ResponseData<Void>> sendInvoice(@PathVariable Long id) {
        invoiceService.sendInvoice(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Invoice sent successfully")
                .build());
    }

    @PutMapping("/{id}/mark-paid")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    @Operation(summary = "Mark invoice as paid")
    public ResponseEntity<ResponseData<Void>> markAsPaid(@PathVariable Long id) {
        invoiceService.markAsPaid(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Invoice marked as paid successfully")
                .build());
    }

    @PutMapping("/{id}/void")
    @PreAuthorize("hasAuthority('SALES_UPDATE')")
    @Operation(summary = "Void invoice")
    public ResponseEntity<ResponseData<Void>> voidInvoice(
            @PathVariable Long id,
            @RequestParam String reason) {
        invoiceService.voidInvoice(id, reason);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Invoice voided successfully")
                .build());
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get overdue invoices")
    public ResponseEntity<ResponseData<List<InvoiceResponse>>> getOverdueInvoices() {
        List<InvoiceResponse> response = invoiceService.getOverdueInvoices();
        return ResponseEntity.ok(ResponseData.<List<InvoiceResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Overdue invoices retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get invoices by date range")
    public ResponseEntity<ResponseData<List<InvoiceResponse>>> getInvoicesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<InvoiceResponse> response = invoiceService.getInvoicesByDateRange(startDate, endDate);
        return ResponseEntity.ok(ResponseData.<List<InvoiceResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Invoices by date range retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Search invoices")
    public ResponseEntity<ResponseData<Page<InvoiceResponse>>> searchInvoices(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<InvoiceResponse> response = invoiceService.searchInvoices(keyword, pageable);
        return ResponseEntity.ok(ResponseData.<Page<InvoiceResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Invoices searched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Generate invoice PDF")
    public ResponseEntity<ResponseData<String>> generateInvoicePDF(@PathVariable Long id) {
        String pdfUrl = invoiceService.generateInvoicePDF(id);
        return ResponseEntity.ok(ResponseData.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Invoice PDF generated successfully")
                .data(pdfUrl)
                .build());
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get invoice statistics")
    public ResponseEntity<ResponseData<InvoiceStatistics>> getInvoiceStatistics() {
        InvoiceStatistics stats = invoiceService.getInvoiceStatistics();
        return ResponseEntity.ok(ResponseData.<InvoiceStatistics>builder()
                .status(HttpStatus.OK.value())
                .message("Invoice statistics retrieved successfully")
                .data(stats)
                .build());
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        return 1L; // Placeholder
    }

    @lombok.Data
    @lombok.Builder
    public static class InvoiceStatistics {
        private long totalInvoices;
        private long draftInvoices;
        private long issuedInvoices;
        private long sentInvoices;
        private long paidInvoices;
        private long voidInvoices;
        private long overdueInvoices;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal outstandingAmount;
    }
}
