package com.example.Backend.dtos.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long id;
    private Long orderId;
    private String orderNo;
    private String method; // cash, card, bank, momo, vnpay, other
    private String status; // pending, completed, failed, refunded, cancelled
    private BigDecimal amount;
    private LocalDateTime paidAt;

    // Gateway fields
    private String gatewayRequestId;
    private String gatewayTransId;
    private String gatewayPartner;
    private String gatewayResultCode;
    private String gatewayMessage;
    private Map<String, Object> gatewayPayload;

    // Related entities
    private CustomerInfo customer;
    private UserInfo createdBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private Long id;
        private String fullName;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String fullName;
        private String username;
    }
}
