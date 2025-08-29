package com.example.Backend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.company.name:CRM Mobile Store}")
    private String companyName;

    @Value("${app.company.logo:https://via.placeholder.com/150x60/2563eb/ffffff?text=CRM+Store}")
    private String logoUrl;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Gửi email OTP cho xác thực tài khoản
     * @param toEmail Email người nhận
     * @param customerName Tên khách hàng
     * @param otp Mã OTP 6 số
     * @param expiryMinutes Thời gian hết hạn OTP (phút)
     */
    public void sendOTPEmailAccountVerification(String toEmail, String customerName, String otp, int expiryMinutes) {
        try {
            log.info("Sending OTP email to: {}", toEmail);

            // Tạo context cho Thymeleaf template
            Context context = new Context();

            // Thông tin khách hàng
            context.setVariable("customerName", customerName != null ? customerName : "Khách hàng");
            context.setVariable("customerEmail", toEmail);

            // Thông tin OTP
            context.setVariable("otpCode", otp);
            context.setVariable("expiryMinutes", expiryMinutes);

            // Tách OTP thành từng ký tự để hiển thị trong các ô riêng biệt
            if (otp != null && otp.length() == 6) {
                context.setVariable("otp_1", String.valueOf(otp.charAt(0)));
                context.setVariable("otp_2", String.valueOf(otp.charAt(1)));
                context.setVariable("otp_3", String.valueOf(otp.charAt(2)));
                context.setVariable("otp_4", String.valueOf(otp.charAt(3)));
                context.setVariable("otp_5", String.valueOf(otp.charAt(4)));
                context.setVariable("otp_6", String.valueOf(otp.charAt(5)));
            } else {
                // Fallback nếu OTP không đúng định dạng
                for (int i = 1; i <= 6; i++) {
                    context.setVariable("otp_" + i, "0");
                }
            }

            // Thông tin công ty
            context.setVariable("logoUrl", logoUrl);
            context.setVariable("companyName", companyName);

            // URL xác thực (có thể được sử dụng nếu có trang xác thực web)
            String verifyUrl = baseUrl + "/verify-otp?email=" + toEmail;
            context.setVariable("verifyUrl", verifyUrl);

            // Process template với context
            String htmlContent = templateEngine.process("otp-email", context);

            // Tạo và gửi email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Mã xác thực OTP - " + companyName);
            helper.setText(htmlContent, true); // true = HTML content

            mailSender.send(message);

            log.info("OTP email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email OTP: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while sending OTP email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Lỗi không xác định khi gửi email OTP: " + e.getMessage(), e);
        }
    }

    /**
     * Gửi email OTP đơn giản (overloaded method)
     * @param toEmail Email người nhận
     * @param otp Mã OTP 6 số
     */
    public void sendOTPEmailAccountVerification(String toEmail, String otp) {
        sendOTPEmailAccountVerification(toEmail, null, otp, 10); // Default 10 phút
    }

    /**
     * Gửi email chào mừng
     * @param toEmail Email người nhận
     * @param customerName Tên khách hàng
     */
    public void sendWelcomeEmail(String toEmail, String customerName) {
        try {
            log.info("Sending welcome email to: {}", toEmail);

            Context context = new Context();
            context.setVariable("customerName", customerName != null ? customerName : "Khách hàng");
            context.setVariable("logoUrl", logoUrl);
            context.setVariable("companyName", companyName);

            String htmlContent = templateEngine.process("welcome-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Chào mừng bạn đến với " + companyName);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Welcome email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email chào mừng: " + e.getMessage(), e);
        }
    }

    /**
     * Gửi email reset mật khẩu
     * @param toEmail Email người nhận
     * @param customerName Tên khách hàng
     * @param resetToken Token reset
     */
    public void sendPasswordResetEmail(String toEmail, String customerName, String resetToken) {
        try {
            log.info("Sending password reset email to: {}", toEmail);

            Context context = new Context();
            context.setVariable("customerName", customerName != null ? customerName : "Khách hàng");
            context.setVariable("resetToken", resetToken);
            context.setVariable("logoUrl", logoUrl);
            context.setVariable("companyName", companyName);

            String resetUrl = baseUrl + "/reset-password?token=" + resetToken;
            context.setVariable("resetUrl", resetUrl);

            String htmlContent = templateEngine.process("password-reset-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Đặt lại mật khẩu - " + companyName);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Password reset email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email reset mật khẩu: " + e.getMessage(), e);
        }
    }

    /**
     * Gửi email xác nhận đơn hàng
     * @param toEmail Email người nhận
     * @param customerName Tên khách hàng
     * @param orderNumber Số đơn hàng
     * @param orderDetails Chi tiết đơn hàng
     */
    public void sendOrderConfirmationEmail(String toEmail, String customerName, String orderNumber, Object orderDetails) {
        try {
            log.info("Sending order confirmation email to: {} for order: {}", toEmail, orderNumber);

            Context context = new Context();
            context.setVariable("customerName", customerName != null ? customerName : "Khách hàng");
            context.setVariable("orderNumber", orderNumber);
            context.setVariable("orderDetails", orderDetails);
            context.setVariable("logoUrl", logoUrl);
            context.setVariable("companyName", companyName);

            String htmlContent = templateEngine.process("order-confirmation-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Xác nhận đơn hàng #" + orderNumber + " - " + companyName);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Order confirmation email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send order confirmation email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email xác nhận đơn hàng: " + e.getMessage(), e);
        }
    }
    /**
     * Gửi email thông báo bảo hành
     * @param toEmail Email người nhận
     * @param customerName Tên khách hàng
     * @param warrantyDetails Chi tiết bảo hành
     */
    public void sendWarrantyNotificationEmail(String toEmail, String customerName, Object warrantyDetails) {
        try {
            log.info("Sending warranty notification email to: {}", toEmail);

            Context context = new Context();
            context.setVariable("customerName", customerName != null ? customerName : "Khách hàng");
            context.setVariable("warrantyDetails", warrantyDetails);
            context.setVariable("logoUrl", logoUrl);
            context.setVariable("companyName", companyName);

            String htmlContent = templateEngine.process("warranty-notification-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Thông báo bảo hành - " + companyName);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Warranty notification email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send warranty notification email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email thông báo bảo hành: " + e.getMessage(), e);
        }
    }
    /**
     * Gửi email đơn giản (text)
     * @param toEmail Email người nhận
     * @param subject Tiêu đề
     * @param message Nội dung
     */
    public void sendSimpleEmail(String toEmail, String subject, String message) {
        try {
            log.info("Sending simple email to: {}", toEmail);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(toEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);

            log.info("Simple email sent successfully to: {}", toEmail);

        } catch (MailException e) {
            log.error("Failed to send simple email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email: " + e.getMessage(), e);
        }
    }
    /**
     * Kiểm tra cấu hình email
     * @return true nếu cấu hình hợp lệ
     */
    public boolean isEmailConfigured() {
        try {
            return fromEmail != null && !fromEmail.isEmpty() &&
                   mailSender != null && templateEngine != null;
        } catch (Exception e) {
            log.error("Error checking email configuration: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gửi email khuyến mãi
     * @param toEmail Email người nhận
     * @param customerName Tên khách hàng
     * @param promotionTitle Tiêu đề khuyến mãi
     * @param promotionDetails Chi tiết khuyến mãi
     */
    public void sendPromotionEmail(String toEmail, String customerName, String promotionTitle, String promotionDetails) {
        try {
            log.info("Sending promotion email to: {}", toEmail);

            Context context = new Context();
            context.setVariable("customerName", customerName != null ? customerName : "Khách hàng");
            context.setVariable("promotionTitle", promotionTitle);
            context.setVariable("promotionDetails", promotionDetails);
            context.setVariable("logoUrl", logoUrl);
            context.setVariable("companyName", companyName);

            // Sử dụng welcome-email template tạm thời cho promotion
            String htmlContent = templateEngine.process("welcome-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🎉 " + promotionTitle + " - " + companyName);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Promotion email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send promotion email to: {}. Error: {}", toEmail, e.getMessage(), e);
        }
    }


}
