package com.example.Backend.dtos.order;

import com.example.Backend.models.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateDTO {

    @NotNull(message = "ID khách hàng không được để trống")
    @Min(value = 1, message = "ID khách hàng không hợp lệ")
    private Long customerId;

    @Valid
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    @Size(min = 1, max = 50, message = "Số lượng sản phẩm phải từ 1 đến 50")
    private List<OrderItemCreateDTO> items;

    @DecimalMin(value = "0.0", message = "Giảm giá phải >= 0")
    private BigDecimal discount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Thuế phải >= 0")
    private BigDecimal tax = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Phí vận chuyển phải >= 0")
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    private String note;

    // Thông tin giao hàng
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    @Size(max = 255, message = "Địa chỉ giao hàng không được vượt quá 255 ký tự")
    private String shippingAddress;

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 100, message = "Tên người nhận không được vượt quá 100 ký tự")
    private String shippingName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    @Pattern(regexp = "^[0-9+\\-\\s()]{10,20}$", message = "Số điện thoại không hợp lệ")
    private String shippingPhone;

    // Thông tin thanh toán
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private Order.PaymentMethod paymentMethod;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemCreateDTO {

        @NotNull(message = "ID sản phẩm không được để trống")
        @Min(value = 1, message = "ID sản phẩm không hợp lệ")
        private Long productId;

        @Min(value = 1, message = "ID variant không hợp lệ")
        private Long variantId;

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải >= 1")
        @Max(value = 1000, message = "Số lượng không được vượt quá 1000")
        private Integer quantity;

        @DecimalMin(value = "0.0", message = "Giảm giá phải >= 0")
        private BigDecimal discountAmount = BigDecimal.ZERO;
    }
}
