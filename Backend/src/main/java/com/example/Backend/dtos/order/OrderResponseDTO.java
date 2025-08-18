package com.example.Backend.dtos.order;

import com.example.Backend.models.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;
    private String orderNo;
    private Order.OrderStatus status;
    private String statusDisplayName;

    // Thông tin khách hàng
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // Thông tin người tạo
    private Long createdById;
    private String createdByName;

    // Thông tin tài chính
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal shippingFee;
    private BigDecimal total;

    // Thông tin giao hàng
    private String shippingAddress;
    private String shippingName;
    private String shippingPhone;

    // Thông tin thanh toán
    private Order.PaymentMethod paymentMethod;
    private String paymentMethodDisplayName;
    private Order.PaymentStatus paymentStatus;
    private String paymentStatusDisplayName;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    // Ghi chú và lý do
    private String note;
    private String cancelReason;

    // Danh sách sản phẩm
    private List<OrderItemResponseDTO> items;

    // Thống kê
    private Integer totalItems;
    private Integer totalQuantity;

    // Trạng thái logic
    private Boolean isPaid;
    private Boolean isDelivered;
    private Boolean isCancelled;
    private Boolean canBeCancelled;
    private Boolean canBeReturned;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponseDTO {

        private Long id;

        // Thông tin sản phẩm
        private Long productId;
        private String productName;
        private String productSku;
        private String productImageUrl;

        // Thông tin variant
        private Long variantId;
        private String variantColor;
        private Integer variantStorageGb;
        private Integer variantRamGb;
        private String variantSku;

        // Thông tin đơn hàng
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal discountAmount;
        private BigDecimal lineTotal;

        // Brand và Model info
        private String brandName;
        private String modelName;
    }
}
