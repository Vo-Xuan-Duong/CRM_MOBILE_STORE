package com.example.Backend.listeners;

import com.example.Backend.events.*;
import com.example.Backend.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailEventListener {

    private final EmailService emailService;

    public EmailEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Processing user registration email for: {}", event.getEmail());
        try {
            emailService.sendWelcomeEmail(event.getEmail(), event.getFullName());
            log.info("Welcome email sent successfully to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", event.getEmail(), e);
        }
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Processing order confirmation email for: {}", event.getCustomerEmail());
        try {
            emailService.sendOrderConfirmationEmail(
                event.getCustomerEmail(),
                event.getCustomerName(),
                event.getOrderNumber(),
                event.getOrderDetails()
            );
            log.info("Order confirmation email sent successfully to: {}", event.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to: {}", event.getCustomerEmail(), e);
        }
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Processing order status update email for: {}", event.getCustomerEmail());
        try {
            emailService.sendOrderStatusUpdateEmail(
                event.getCustomerEmail(),
                event.getCustomerName(),
                event.getOrderNumber(),
                event.getNewStatus(),
                event.getStatusMessage()
            );
            log.info("Order status update email sent successfully to: {}", event.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send order status update email to: {}", event.getCustomerEmail(), e);
        }
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void handlePasswordResetRequested(PasswordResetRequestedEvent event) {
        log.info("Processing password reset email for: {}", event.getEmail());
        try {
            emailService.sendPasswordResetEmail(
                event.getEmail(),
                event.getUserName(),
                event.getResetToken()
            );
            log.info("Password reset email sent successfully to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", event.getEmail(), e);
        }
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void handleWarrantyNotification(WarrantyNotificationEvent event) {
        log.info("Processing warranty notification email for: {}", event.getCustomerEmail());
        try {
            emailService.sendWarrantyNotificationEmail(
                event.getCustomerEmail(),
                event.getCustomerName(),
                event.getProductName(),
                event.getWarrantyInfo()
            );
            log.info("Warranty notification email sent successfully to: {}", event.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send warranty notification email to: {}", event.getCustomerEmail(), e);
        }
    }
}
