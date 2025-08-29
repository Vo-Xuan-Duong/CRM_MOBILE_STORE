package com.example.Backend.dtos.momo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MomoResponse {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private Integer resultCode;
    private String message;
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
    private Long responseTime;
    private String signature;
}
