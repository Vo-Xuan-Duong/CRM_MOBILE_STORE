package com.example.Backend.dtos.order;

import com.example.Backend.models.Order;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderUpdateDTO {

    private Order.OrderStatus status;

    @DecimalMin(value = "0.0", message = "Giảm giá phải >= 0")
    private BigDecimal discount;

    @DecimalMin(value = "0.0", message = "Thuế phải >= 0")
    private BigDecimal tax;

    @DecimalMin(value = "0.0", message = "Phí vận chuyển phải >= 0")
    private BigDecimal shippingFee;

    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    private String note;

    // Thông tin giao hàng
    @Size(max = 255, message = "Địa chỉ giao hàng không được vượt quá 255 ký tự")
    private String shippingAddress;

    @Size(max = 100, message = "Tên người nhận không được vượt quá 100 ký tự")
    private String shippingName;

    @Pattern(regexp = "^[0-9+\\-\\s()]{10,20}$", message = "Số điện thoại không hợp lệ")
    private String shippingPhone;

    // Thông tin thanh toán
    private Order.PaymentMethod paymentMethod;
    private Order.PaymentStatus paymentStatus;

    @Size(max = 500, message = "Lý do hủy không được vượt quá 500 ký tự")
    private String cancelReason;
}
