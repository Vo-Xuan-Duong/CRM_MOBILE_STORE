package com.example.Backend.services;

import com.example.Backend.dtos.momo.MomoRequest;
import com.example.Backend.dtos.momo.MomoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomoService {

    @Value("${momo.partnerCode}")
    private String PARTNER_CODE;
    @Value("${momo.partnerName}")
    private String PARTNER_NAME;
    @Value("${momo.accessKey}")
    private String ACCESS_KEY;
    @Value("${momo.secretKey}")
    private String SECRET_KEY;
    @Value("${momo.redirectUrl}")
    private String REDIRECT_URL;
    @Value("${momo.ipnUrl}")
    private String IPN_URL;
    @Value("${momo.endPoint}")
    private String ENDPOINT;



    private final RestTemplate restTemplate;

    public MomoResponse createOrder(String orderId, long amount, String orderInfo) {
        String requestId = UUID.randomUUID().toString();
        String extraData = ""; // "" chứ không phải null

        // requestType thường là "captureWallet"
        final String REQUEST_TYPE = "captureWallet";

        // CHÚ Ý: các giá trị phải đúng y hệt với body JSON
        String rawSignature =
                "accessKey=" + ACCESS_KEY +
                        "&amount=" + amount +
                        "&extraData=" + extraData +
                        "&ipnUrl=" + IPN_URL +
                        "&orderId=" + orderId +
                        "&orderInfo=" + orderInfo +
                        "&partnerCode=" + PARTNER_CODE +
                        "&redirectUrl=" + REDIRECT_URL +
                        "&requestId=" + requestId +
                        "&requestType=" + REQUEST_TYPE;

        String signature = hmacSHA256(SECRET_KEY, rawSignature);

        MomoRequest req = MomoRequest.builder()
                .partnerCode(PARTNER_CODE)
                .requestId(requestId)
                .amount(String.valueOf(amount))
                .orderId(orderId)
                .orderInfo(orderInfo)
                .redirectUrl(REDIRECT_URL)
                .ipnUrl(IPN_URL)
                .requestType(REQUEST_TYPE)
                .extraData(extraData)
                .lang("vi")
                .signature(signature)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<MomoResponse> resp =
                restTemplate.postForEntity(ENDPOINT, new HttpEntity<>(req, headers), MomoResponse.class);

        return resp.getBody();
    }



    public String hmacSHA256(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // convert sang hex lowercase
            StringBuilder hex = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC error", e);
        }
    }

}
