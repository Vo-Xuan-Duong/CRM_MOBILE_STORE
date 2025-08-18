package com.example.Backend.dtos.order;

import com.example.Backend.models.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderSearchRequest {

    private String keyword; // Tìm theo orderNo, customer name, phone
    private Long customerId;
    private Long createdById;

    // Lọc theo trạng thái
    private List<Order.OrderStatus> statuses;
    private List<Order.PaymentStatus> paymentStatuses;
    private List<Order.PaymentMethod> paymentMethods;

    // Lọc theo thời gian
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private LocalDateTime paidFrom;
    private LocalDateTime paidTo;
    private LocalDateTime deliveredFrom;
    private LocalDateTime deliveredTo;

    // Lọc theo giá trị
    private BigDecimal totalFrom;
    private BigDecimal totalTo;

    // Lọc theo sản phẩm
    private Long productId;
    private Long brandId;
    private Long modelId;

    // Phân trang và sắp xếp
    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 10;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDirection = "DESC";
}
