package com.example.Backend.services;

import com.example.Backend.dtos.email.EmailRequest;
import com.example.Backend.dtos.email.EmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.base-url}")
    private String baseUrl;

    @Value("${app.email.logo-url}")
    private String logoUrl;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Gửi email đơn giản (text)
     */
    public EmailResponse sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", to);

            return EmailResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .to(to)
                    .subject(subject)
                    .status("SENT")
                    .message("Email sent successfully")
                    .sentAt(LocalDateTime.now())
                    .success(true)
                    .build();

        } catch (MailException e) {
            log.error("Failed to send simple email to: {}", to, e);
            return createErrorResponse(to, subject, e.getMessage());
        }
    }

    /**
     * Gửi email HTML với template
     */
    public EmailResponse sendHtmlEmail(EmailRequest emailRequest) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());

            // Set CC và BCC nếu có
            if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
                helper.setCc(emailRequest.getCc().toArray(new String[0]));
            }
            if (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()) {
                helper.setBcc(emailRequest.getBcc().toArray(new String[0]));
            }

            // Xử lý template hoặc content trực tiếp
            String emailContent;
            if (emailRequest.getTemplateName() != null) {
                emailContent = processTemplate(emailRequest.getTemplateName(), emailRequest.getTemplateVariables());
            } else {
                emailContent = emailRequest.getContent();
            }

            helper.setText(emailContent, emailRequest.isHtml());

            // Thêm attachments nếu có
            if (emailRequest.getAttachments() != null) {
                for (String attachment : emailRequest.getAttachments()) {
                    addAttachment(helper, attachment);
                }
            }

            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully to: {}", emailRequest.getTo());

            return EmailResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .to(emailRequest.getTo())
                    .subject(emailRequest.getSubject())
                    .status("SENT")
                    .message("Email sent successfully")
                    .sentAt(LocalDateTime.now())
                    .success(true)
                    .build();

        } catch (MessagingException | MailException e) {
            log.error("Failed to send HTML email to: {}", emailRequest.getTo(), e);
            return createErrorResponse(emailRequest.getTo(), emailRequest.getSubject(), e.getMessage());
        }
    }

    /**
     * Gửi email chào mừng cho user mới
     */
    public EmailResponse sendWelcomeEmail(String to, String userName) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("baseUrl", baseUrl);
            context.setVariable("logoUrl", logoUrl);

            String content = templateEngine.process("welcome-email", context);

            EmailRequest request = EmailRequest.builder()
                    .to(to)
                    .subject("Chào mừng bạn đến với CRM Mobile Store!")
                    .content(content)
                    .isHtml(true)
                    .build();

            return sendHtmlEmail(request);

        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
            return createErrorResponse(to, "Welcome Email", e.getMessage());
        }
    }

    /**
     * Gửi email reset password
     */
    public EmailResponse sendPasswordResetEmail(String to, String userName, String resetToken) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("resetUrl", baseUrl + "/reset-password?token=" + resetToken);
            context.setVariable("baseUrl", baseUrl);
            context.setVariable("logoUrl", logoUrl);

            String content = templateEngine.process("password-reset-email", context);

            EmailRequest request = EmailRequest.builder()
                    .to(to)
                    .subject("Yêu cầu đặt lại mật khẩu")
                    .content(content)
                    .isHtml(true)
                    .build();

            return sendHtmlEmail(request);

        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
            return createErrorResponse(to, "Password Reset Email", e.getMessage());
        }
    }

    /**
     * Gửi email xác nhận đơn hàng
     */
    public EmailResponse sendOrderConfirmationEmail(String to, String customerName, String orderNumber, String orderDetails) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("orderNumber", orderNumber);
            context.setVariable("orderDetails", orderDetails);
            context.setVariable("baseUrl", baseUrl);
            context.setVariable("logoUrl", logoUrl);

            String content = templateEngine.process("order-confirmation-email", context);

            EmailRequest request = EmailRequest.builder()
                    .to(to)
                    .subject("Xác nhận đơn hàng #" + orderNumber)
                    .content(content)
                    .isHtml(true)
                    .build();

            return sendHtmlEmail(request);

        } catch (Exception e) {
            log.error("Failed to send order confirmation email to: {}", to, e);
            return createErrorResponse(to, "Order Confirmation Email", e.getMessage());
        }
    }

    /**
     * Gửi email thông báo bảo hành
     */
    public EmailResponse sendWarrantyNotificationEmail(String to, String customerName, String productName, String warrantyInfo) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("productName", productName);
            context.setVariable("warrantyInfo", warrantyInfo);
            context.setVariable("baseUrl", baseUrl);
            context.setVariable("logoUrl", logoUrl);

            String content = templateEngine.process("warranty-notification-email", context);

            EmailRequest request = EmailRequest.builder()
                    .to(to)
                    .subject("Thông báo bảo hành sản phẩm " + productName)
                    .content(content)
                    .isHtml(true)
                    .build();

            return sendHtmlEmail(request);

        } catch (Exception e) {
            log.error("Failed to send warranty notification email to: {}", to, e);
            return createErrorResponse(to, "Warranty Notification Email", e.getMessage());
        }
    }

    /**
     * Xử lý template với variables
     */
    private String processTemplate(String templateName, Object variables) {
        Context context = new Context();
        if (variables instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> variableMap = (java.util.Map<String, Object>) variables;
            variableMap.forEach(context::setVariable);
        }

        // Thêm các biến mặc định
        context.setVariable("baseUrl", baseUrl);
        context.setVariable("logoUrl", logoUrl);

        return templateEngine.process(templateName, context);
    }

    /**
     * Thêm attachment vào email
     */
    private void addAttachment(MimeMessageHelper helper, String attachmentPath) throws MessagingException {
        try {
            File file = new File(attachmentPath);
            if (file.exists()) {
                FileSystemResource fileResource = new FileSystemResource(file);
                helper.addAttachment(file.getName(), fileResource);
            } else {
                // Thử tìm trong classpath
                ClassPathResource classPathResource = new ClassPathResource(attachmentPath);
                if (classPathResource.exists()) {
                    helper.addAttachment(attachmentPath, classPathResource);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to add attachment: {}", attachmentPath, e);
        }
    }

    /**
     * Tạo response lỗi
     */
    private EmailResponse createErrorResponse(String to, String subject, String errorMessage) {
        return EmailResponse.builder()
                .id(UUID.randomUUID().toString())
                .to(to)
                .subject(subject)
                .status("FAILED")
                .message("Failed to send email: " + errorMessage)
                .sentAt(LocalDateTime.now())
                .success(false)
                .build();
    }

    /**
     * Kiểm tra cấu hình email
     */
    public boolean isEmailConfigured() {
        try {
            return mailSender != null && fromEmail != null && !fromEmail.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gửi email hàng loạt
     */
    public List<EmailResponse> sendBulkEmails(com.example.Backend.dtos.email.BulkEmailRequest bulkRequest) {
        List<EmailResponse> responses = new ArrayList<>();

        for (String recipient : bulkRequest.getRecipients()) {
            try {
                EmailRequest emailRequest = EmailRequest.builder()
                        .to(recipient)
                        .subject(bulkRequest.getSubject())
                        .content(bulkRequest.getContent())
                        .templateName(bulkRequest.getTemplateName())
                        .templateVariables(bulkRequest.getTemplateVariables())
                        .isHtml(bulkRequest.isHtml())
                        .build();

                EmailResponse response = sendHtmlEmail(emailRequest);
                responses.add(response);

                // Thêm delay nhỏ để tránh spam
                Thread.sleep(100);

            } catch (Exception e) {
                log.error("Failed to send bulk email to: {}", recipient, e);
                responses.add(createErrorResponse(recipient, bulkRequest.getSubject(), e.getMessage()));
            }
        }

        return responses;
    }

    /**
     * Gửi email marketing/promotion
     */
    public EmailResponse sendPromotionEmail(String to, String customerName, String promotionTitle, String promotionDetails) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("promotionTitle", promotionTitle);
            context.setVariable("promotionDetails", promotionDetails);
            context.setVariable("baseUrl", baseUrl);
            context.setVariable("logoUrl", logoUrl);

            String content = templateEngine.process("promotion-email", context);

            EmailRequest request = EmailRequest.builder()
                    .to(to)
                    .subject("🎉 " + promotionTitle + " - Ưu đãi đặc biệt!")
                    .content(content)
                    .isHtml(true)
                    .build();

            return sendHtmlEmail(request);

        } catch (Exception e) {
            log.error("Failed to send promotion email to: {}", to, e);
            return createErrorResponse(to, "Promotion Email", e.getMessage());
        }
    }

    /**
     * Gửi email thông báo trạng thái đơn hàng
     */
    public EmailResponse sendOrderStatusUpdateEmail(String to, String customerName, String orderNumber, String newStatus, String statusMessage) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("orderNumber", orderNumber);
            context.setVariable("newStatus", newStatus);
            context.setVariable("statusMessage", statusMessage);
            context.setVariable("baseUrl", baseUrl);
            context.setVariable("logoUrl", logoUrl);

            String content = templateEngine.process("order-status-update-email", context);

            EmailRequest request = EmailRequest.builder()
                    .to(to)
                    .subject("Cập nhật trạng thái đơn hàng #" + orderNumber)
                    .content(content)
                    .isHtml(true)
                    .build();

            return sendHtmlEmail(request);

        } catch (Exception e) {
            log.error("Failed to send order status update email to: {}", to, e);
            return createErrorResponse(to, "Order Status Update Email", e.getMessage());
        }
    }
}
