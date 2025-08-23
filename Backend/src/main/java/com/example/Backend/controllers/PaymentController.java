package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.payment.PaymentCreateDTO;
import com.example.Backend.dtos.payment.PaymentResponseDTO;
import com.example.Backend.models.Payment;
import com.example.Backend.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Management", description = "APIs for managing payments and refunds")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create new payment", description = "Create a new payment record")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    public ResponseEntity<ResponseData<PaymentResponseDTO>> createPayment(
            @Valid @RequestBody PaymentCreateDTO createDTO,
            Authentication authentication) {
        log.info("Creating payment for order ID: {}", createDTO.getOrderId());

        PaymentResponseDTO payment = paymentService.createPayment(createDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<PaymentResponseDTO>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Payment created successfully")
                        .data(payment)
                        .build());
    }

    @PutMapping("/{id}/process")
    @Operation(summary = "Process payment", description = "Process a pending payment")
    @PreAuthorize("hasAuthority('PAYMENT_PROCESS')")
    public ResponseEntity<ResponseData<PaymentResponseDTO>> processPayment(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Parameter(description = "Gateway response") @RequestParam(required = false) String gatewayResponse) {
        log.info("Processing payment with ID: {}", id);

        PaymentResponseDTO payment = paymentService.processPayment(id);

        return ResponseEntity.ok(ResponseData.<PaymentResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Payment processed successfully")
                .data(payment)
                .build());
    }

    @PutMapping("/{id}/refund")
    @Operation(summary = "Refund payment", description = "Process a payment refund")
    @PreAuthorize("hasAuthority('PAYMENT_REFUND')")
    public ResponseEntity<ResponseData<PaymentResponseDTO>> refundPayment(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Parameter(description = "Refund amount") @RequestParam BigDecimal refundAmount,
            @Parameter(description = "Refund reason") @RequestParam String refundReason) {
        log.info("Processing refund for payment ID: {} with amount: {}", id, refundAmount);

        PaymentResponseDTO payment = paymentService.refundPayment(id, refundAmount);

        return ResponseEntity.ok(ResponseData.<PaymentResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Refund processed successfully")
                .data(payment)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieve payment information by ID")
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public ResponseEntity<ResponseData<PaymentResponseDTO>> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable Long id) {
        log.info("Getting payment with ID: {}", id);

        PaymentResponseDTO payment = paymentService.getPaymentById(id);

        return ResponseEntity.ok(ResponseData.<PaymentResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Payment retrieved successfully")
                .data(payment)
                .build());
    }

//    @GetMapping("/search")
//    @Operation(summary = "Search payments", description = "Search payments by keyword")
//    @PreAuthorize("hasAuthority('PAYMENT_READ')")
//    public ResponseEntity<ResponseData<Page<PaymentResponseDTO>>> searchPayments(
//            @Parameter(description = "Search keyword") @RequestParam(required = false) String keyword,
//            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
//            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
//            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
//
//        Sort sort = sortDir.equalsIgnoreCase("desc") ?
//                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<PaymentResponseDTO> payments = paymentService.searchPayments(keyword, pageable);
//
//        return ResponseEntity.ok(ResponseData.<Page<PaymentResponseDTO>>builder()
//                .status(HttpStatus.OK.value())
//                .message("Payments retrieved successfully")
//                .data(payments)
//                .build());
//    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get payments by customer", description = "Get all payments for a specific customer")
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public ResponseEntity<ResponseData<Page<PaymentResponseDTO>>> getPaymentsByCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PaymentResponseDTO> payments = paymentService.getPaymentsByCustomer(customerId, pageable);

        return ResponseEntity.ok(ResponseData.<Page<PaymentResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Customer payments retrieved successfully")
                .data(payments)
                .build());
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payments by order", description = "Get all payments for a specific order")
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public ResponseEntity<ResponseData<Page<PaymentResponseDTO>>> getPaymentsByOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PaymentResponseDTO> payments = paymentService.getPaymentsByOrder(orderId, pageable);

        return ResponseEntity.ok(ResponseData.<Page<PaymentResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Order payments retrieved successfully")
                .data(payments)
                .build());
    }

    @GetMapping("/reports/revenue")
    @Operation(summary = "Get revenue report", description = "Get total revenue for date range")
    @PreAuthorize("hasAuthority('PAYMENT_REPORT')")
    public ResponseEntity<ResponseData<BigDecimal>> getRevenueReport(
            @Parameter(description = "Start date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        BigDecimal totalRevenue = paymentService.getTotalPaymentsByDateRange(startDate, endDate);

        return ResponseEntity.ok(ResponseData.<BigDecimal>builder()
                .status(HttpStatus.OK.value())
                .message("Revenue report generated successfully")
                .data(totalRevenue)
                .build());
    }

    @GetMapping("/reports/revenue/method")
    @Operation(summary = "Get revenue by payment method", description = "Get total revenue by payment method for date range")
    @PreAuthorize("hasAuthority('PAYMENT_REPORT')")
    public ResponseEntity<ResponseData<BigDecimal>> getRevenueByMethod(
            @Parameter(description = "Payment method") @RequestParam Payment.PaymentMethod method,
            @Parameter(description = "Start date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        BigDecimal totalRevenue = paymentService.getTotalPaymentsByMethodAndDateRange(method, startDate, endDate);

        return ResponseEntity.ok(ResponseData.<BigDecimal>builder()
                .status(HttpStatus.OK.value())
                .message("Revenue by method report generated successfully")
                .data(totalRevenue)
                .build());
    }
}
