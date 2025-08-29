package com.example.Backend.dtos.momo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MomoRequest {
    private String partnerCode;
    private String partnerName; // optional
    private String storeId;     // optional
    private String requestId;
    private String amount;
    private String orderId;
    private String orderInfo;
    private String ipnUrl;
    private String redirectUrl;
    private String requestType;   // e.g. "captureWallet"
    private String extraData;     // base64 nếu cần
    private String lang;          // "vi" | "en"
    private String signature;     // HMAC-SHA256
}
