package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.email.EmailRequest;
import com.example.Backend.dtos.email.EmailResponse;
import com.example.Backend.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@Tag(name = "Email Management", description = "APIs for sending emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-simple")
    @Operation(summary = "Send simple text email")
    @PreAuthorize("hasAuthority('EMAIL_SEND')")
    public ResponseEntity<ResponseData<EmailResponse>> sendSimpleEmail(
            @Parameter(description = "Recipient email") @RequestParam String to,
            @Parameter(description = "Email subject") @RequestParam String subject,
            @Parameter(description = "Email content") @RequestParam String content) {

        EmailResponse response = emailService.sendSimpleEmail(to, subject, content);

        return ResponseEntity.ok(ResponseData.<EmailResponse>builder()
                .status(response.isSuccess() ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(response.isSuccess() ? "Email sent successfully" : "Failed to send email")
                .data(response)
                .build());
    }

    @PostMapping("/send-html")
    @Operation(summary = "Send HTML email with template support")
    @PreAuthorize("hasAuthority('EMAIL_SEND')")
    public ResponseEntity<ResponseData<EmailResponse>> sendHtmlEmail(
            @Valid @RequestBody EmailRequest emailRequest) {

        EmailResponse response = emailService.sendHtmlEmail(emailRequest);

        return ResponseEntity.ok(ResponseData.<EmailResponse>builder()
                .status(response.isSuccess() ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(response.isSuccess() ? "Email sent successfully" : "Failed to send email")
                .data(response)
                .build());
    }

    @PostMapping("/send-welcome")
    @Operation(summary = "Send welcome email to new user")
    @PreAuthorize("hasAuthority('EMAIL_SEND')")
    public ResponseEntity<ResponseData<EmailResponse>> sendWelcomeEmail(
            @Parameter(description = "Recipient email") @RequestParam String to,
            @Parameter(description = "User name") @RequestParam String userName) {

        EmailResponse response = emailService.sendWelcomeEmail(to, userName);

        return ResponseEntity.ok(ResponseData.<EmailResponse>builder()
                .status(response.isSuccess() ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(response.isSuccess() ? "Welcome email sent successfully" : "Failed to send welcome email")
                .data(response)
                .build());
    }

    @PostMapping("/send-password-reset")
    @Operation(summary = "Send password reset email")
    @PreAuthorize("hasAuthority('EMAIL_SEND')")
    public ResponseEntity<ResponseData<EmailResponse>> sendPasswordResetEmail(
            @Parameter(description = "Recipient email") @RequestParam String to,
            @Parameter(description = "User name") @RequestParam String userName,
            @Parameter(description = "Reset token") @RequestParam String resetToken) {

        EmailResponse response = emailService.sendPasswordResetEmail(to, userName, resetToken);

        return ResponseEntity.ok(ResponseData.<EmailResponse>builder()
                .status(response.isSuccess() ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(response.isSuccess() ? "Password reset email sent successfully" : "Failed to send password reset email")
                .data(response)
                .build());
    }

    @PostMapping("/send-order-confirmation")
    @Operation(summary = "Send order confirmation email")
    @PreAuthorize("hasAuthority('EMAIL_SEND')")
    public ResponseEntity<ResponseData<EmailResponse>> sendOrderConfirmationEmail(
            @Parameter(description = "Recipient email") @RequestParam String to,
            @Parameter(description = "Customer name") @RequestParam String customerName,
            @Parameter(description = "Order number") @RequestParam String orderNumber,
            @Parameter(description = "Order details") @RequestParam String orderDetails) {

        EmailResponse response = emailService.sendOrderConfirmationEmail(to, customerName, orderNumber, orderDetails);

        return ResponseEntity.ok(ResponseData.<EmailResponse>builder()
                .status(response.isSuccess() ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(response.isSuccess() ? "Order confirmation email sent successfully" : "Failed to send order confirmation email")
                .data(response)
                .build());
    }

    @PostMapping("/send-warranty-notification")
    @Operation(summary = "Send warranty notification email")
    @PreAuthorize("hasAuthority('EMAIL_SEND')")
    public ResponseEntity<ResponseData<EmailResponse>> sendWarrantyNotificationEmail(
            @Parameter(description = "Recipient email") @RequestParam String to,
            @Parameter(description = "Customer name") @RequestParam String customerName,
            @Parameter(description = "Product name") @RequestParam String productName,
            @Parameter(description = "Warranty information") @RequestParam String warrantyInfo) {

        EmailResponse response = emailService.sendWarrantyNotificationEmail(to, customerName, productName, warrantyInfo);

        return ResponseEntity.ok(ResponseData.<EmailResponse>builder()
                .status(response.isSuccess() ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(response.isSuccess() ? "Warranty notification email sent successfully" : "Failed to send warranty notification email")
                .data(response)
                .build());
    }

    @GetMapping("/config/check")
    @Operation(summary = "Check email configuration status")
    @PreAuthorize("hasAuthority('EMAIL_VIEW')")
    public ResponseEntity<ResponseData<Boolean>> checkEmailConfiguration() {
        boolean isConfigured = emailService.isEmailConfigured();

        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message(isConfigured ? "Email configuration is valid" : "Email configuration is invalid")
                .data(isConfigured)
                .build());
    }

    @PostMapping("/send-bulk")
    @Operation(summary = "Send bulk emails")
    @PreAuthorize("hasAuthority('EMAIL_SEND')")
    public ResponseEntity<ResponseData<List<EmailResponse>>> sendBulkEmails(
            @Valid @RequestBody com.example.Backend.dtos.email.BulkEmailRequest bulkRequest) {

        List<EmailResponse> responses = emailService.sendBulkEmails(bulkRequest);

        long successCount = responses.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        long failureCount = responses.size() - successCount;

        return ResponseEntity.ok(ResponseData.<List<EmailResponse>>builder()
                .status(HttpStatus.OK.value())
                .message(String.format("Bulk email completed. Success: %d, Failed: %d", successCount, failureCount))
                .data(responses)
                .build());
    }

    @PostMapping("/send-promotion")
    @Operation(summary = "Send promotion email")
    @PreAuthorize("hasAuthority('EMAIL_SEND')")
    public ResponseEntity<ResponseData<EmailResponse>> sendPromotionEmail(
            @Parameter(description = "Recipient email") @RequestParam String to,
            @Parameter(description = "Customer name") @RequestParam String customerName,
            @Parameter(description = "Promotion title") @RequestParam String promotionTitle,
            @Parameter(description = "Promotion details") @RequestParam String promotionDetails) {

        EmailResponse response = emailService.sendPromotionEmail(to, customerName, promotionTitle, promotionDetails);

        return ResponseEntity.ok(ResponseData.<EmailResponse>builder()
                .status(response.isSuccess() ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(response.isSuccess() ? "Promotion email sent successfully" : "Failed to send promotion email")
                .data(response)
                .build());
    }

    @PostMapping("/send-order-status-update")
    @Operation(summary = "Send order status update email")
    @PreAuthorize("hasAuthority('EMAIL_SEND')")
    public ResponseEntity<ResponseData<EmailResponse>> sendOrderStatusUpdateEmail(
            @Parameter(description = "Recipient email") @RequestParam String to,
            @Parameter(description = "Customer name") @RequestParam String customerName,
            @Parameter(description = "Order number") @RequestParam String orderNumber,
            @Parameter(description = "New status") @RequestParam String newStatus,
            @Parameter(description = "Status message") @RequestParam String statusMessage) {

        EmailResponse response = emailService.sendOrderStatusUpdateEmail(to, customerName, orderNumber, newStatus, statusMessage);

        return ResponseEntity.ok(ResponseData.<EmailResponse>builder()
                .status(response.isSuccess() ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(response.isSuccess() ? "Order status update email sent successfully" : "Failed to send order status update email")
                .data(response)
                .build());
    }
}
