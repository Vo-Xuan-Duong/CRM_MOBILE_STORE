package com.example.Backend.mappers;

import com.example.Backend.dtos.order.*;
import com.example.Backend.models.*;
import com.example.Backend.repositorys.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;

    /**
     * Chuyển đổi từ OrderCreateDTO sang Order entity
     */
    public Order toEntity(OrderCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        return Order.builder()
                .discount(createDTO.getDiscount() != null ? createDTO.getDiscount() : BigDecimal.ZERO)
                .tax(createDTO.getTax() != null ? createDTO.getTax() : BigDecimal.ZERO)
                .shippingFee(createDTO.getShippingFee() != null ? createDTO.getShippingFee() : BigDecimal.ZERO)
                .note(createDTO.getNote())
                .shippingAddress(createDTO.getShippingAddress())
                .shippingName(createDTO.getShippingName())
                .shippingPhone(createDTO.getShippingPhone())
                .paymentMethod(createDTO.getPaymentMethod())
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .build();
    }

    /**
     * Chuyển đổi từ Order entity sang OrderResponseDTO
     */
    public OrderResponseDTO toResponseDTO(Order order) {
        return toResponseDTO(order, true);
    }

    /**
     * Chuyển đổi từ Order entity sang OrderResponseDTO với tùy chọn include items
     */
    public OrderResponseDTO toResponseDTO(Order order, boolean includeItems) {
        if (order == null) {
            return null;
        }

        OrderResponseDTO.OrderResponseDTOBuilder builder = OrderResponseDTO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .status(order.getStatus())
                .statusDisplayName(order.getStatus().getDisplayName())
                .subtotal(order.getSubtotal())
                .discount(order.getDiscount())
                .tax(order.getTax())
                .shippingFee(order.getShippingFee())
                .total(order.getTotal())
                .shippingAddress(order.getShippingAddress())
                .shippingName(order.getShippingName())
                .shippingPhone(order.getShippingPhone())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .paidAt(order.getPaidAt())
                .deliveredAt(order.getDeliveredAt())
                .cancelledAt(order.getCancelledAt())
                .note(order.getNote())
                .cancelReason(order.getCancelReason())
                .isPaid(order.isPaid())
                .isDelivered(order.isDelivered())
                .isCancelled(order.isCancelled())
                .canBeCancelled(order.canBeCancelled())
                .canBeReturned(order.canBeReturned());

        // Thông tin khách hàng
        if (order.getCustomer() != null) {
            Customer customer = order.getCustomer();
            builder.customerId(customer.getId())
                   .customerName(customer.getFullName())
                   .customerPhone(customer.getPhone())
                   .customerEmail(customer.getEmail());
        }

        // Thông tin người tạo
        if (order.getCreatedBy() != null) {
            User createdBy = order.getCreatedBy();
            builder.createdById(createdBy.getId())
                   .createdByName(createdBy.getFullName());
        }

        // Display names cho enums
        if (order.getPaymentMethod() != null) {
            builder.paymentMethodDisplayName(order.getPaymentMethod().getDisplayName());
        }
        if (order.getPaymentStatus() != null) {
            builder.paymentStatusDisplayName(order.getPaymentStatus().getDisplayName());
        }

        // Include order items nếu được yêu cầu
        if (includeItems) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderIdOrderByIdAsc(order.getId());
            List<OrderResponseDTO.OrderItemResponseDTO> itemDTOs = orderItems.stream()
                    .map(this::toOrderItemResponseDTO)
                    .collect(Collectors.toList());

            builder.items(itemDTOs);
            builder.totalItems(itemDTOs.size());
            builder.totalQuantity(itemDTOs.stream().mapToInt(OrderResponseDTO.OrderItemResponseDTO::getQuantity).sum());
        }

        return builder.build();
    }

    /**
     * Chuyển đổi OrderItem sang OrderItemResponseDTO
     */
    public OrderResponseDTO.OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderResponseDTO.OrderItemResponseDTO.OrderItemResponseDTOBuilder builder =
                OrderResponseDTO.OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .discountAmount(orderItem.getDiscountAmount())
                .lineTotal(orderItem.getLineTotal());

        // Thông tin sản phẩm
        if (orderItem.getProduct() != null) {
            Product product = orderItem.getProduct();
            builder.productId(product.getId())
                   .productName(product.getName())
                   .productSku(product.getSku());

            // Brand và Model info
            if (product.getBrand() != null) {
                builder.brandName(product.getBrand().getName());
            }
            if (product.getModel() != null) {
                builder.modelName(product.getModel().getName());
            }
        }

        // Thông tin variant
        if (orderItem.getVariant() != null) {
            Variant variant = orderItem.getVariant();
            builder.variantId(variant.getId())
                   .variantColor(variant.getColor())
                   .variantStorageGb(variant.getStorageGb())
                   .variantRamGb(variant.getRamGb())
                   .variantSku(variant.getSku());
        }

        return builder.build();
    }

    /**
     * Cập nhật Order entity từ OrderUpdateDTO
     */
    public void updateEntityFromDTO(Order order, OrderUpdateDTO updateDTO) {
        if (order == null || updateDTO == null) {
            return;
        }

        if (updateDTO.getStatus() != null) {
            order.setStatus(updateDTO.getStatus());

            // Cập nhật timestamp tương ứng
            LocalDateTime now = LocalDateTime.now();
            switch (updateDTO.getStatus()) {
                case DELIVERED -> order.setDeliveredAt(now);
                case CANCELLED -> {
                    order.setCancelledAt(now);
                    if (updateDTO.getCancelReason() != null) {
                        order.setCancelReason(updateDTO.getCancelReason());
                    }
                }
            }
        }

        if (updateDTO.getDiscount() != null) {
            order.setDiscount(updateDTO.getDiscount());
        }
        if (updateDTO.getTax() != null) {
            order.setTax(updateDTO.getTax());
        }
        if (updateDTO.getShippingFee() != null) {
            order.setShippingFee(updateDTO.getShippingFee());
        }
        if (updateDTO.getNote() != null) {
            order.setNote(updateDTO.getNote());
        }
        if (updateDTO.getShippingAddress() != null) {
            order.setShippingAddress(updateDTO.getShippingAddress());
        }
        if (updateDTO.getShippingName() != null) {
            order.setShippingName(updateDTO.getShippingName());
        }
        if (updateDTO.getShippingPhone() != null) {
            order.setShippingPhone(updateDTO.getShippingPhone());
        }
        if (updateDTO.getPaymentMethod() != null) {
            order.setPaymentMethod(updateDTO.getPaymentMethod());
        }
        if (updateDTO.getPaymentStatus() != null) {
            order.setPaymentStatus(updateDTO.getPaymentStatus());

            // Cập nhật paidAt nếu thanh toán
            if (Order.PaymentStatus.PAID.equals(updateDTO.getPaymentStatus()) && order.getPaidAt() == null) {
                order.setPaidAt(LocalDateTime.now());
            }
        }

        // Tính lại tổng tiền
        order.calculateTotal();
    }

    /**
     * Chuyển đổi danh sách Order entities sang danh sách OrderResponseDTO
     */
    public List<OrderResponseDTO> toResponseDTOList(List<Order> orders) {
        return toResponseDTOList(orders, false);
    }

    /**
     * Chuyển đổi danh sách Order entities sang danh sách OrderResponseDTO với tùy chọn include items
     */
    public List<OrderResponseDTO> toResponseDTOList(List<Order> orders, boolean includeItems) {
        if (orders == null) {
            return null;
        }
        return orders.stream()
                .map(order -> toResponseDTO(order, includeItems))
                .collect(Collectors.toList());
    }

    /**
     * Tạo OrderResponseDTO tóm tắt (không bao gồm items)
     */
    public OrderResponseDTO toSummaryResponseDTO(Order order) {
        return toResponseDTO(order, false);
    }

    /**
     * Tạo OrderItem entity từ OrderItemCreateDTO
     */
    public OrderItem toOrderItemEntity(OrderCreateDTO.OrderItemCreateDTO itemDTO, Order order) {
        if (itemDTO == null) {
            return null;
        }

        // Lấy thông tin sản phẩm
        Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + itemDTO.getProductId()));

        Variant variant = null;
        if (itemDTO.getVariantId() != null) {
            variant = variantRepository.findById(itemDTO.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy variant với ID: " + itemDTO.getVariantId()));
        }

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .variant(variant)
                .quantity(itemDTO.getQuantity())
                .unitPrice(product.getUnitPrice())
                .discountAmount(itemDTO.getDiscountAmount() != null ? itemDTO.getDiscountAmount() : BigDecimal.ZERO)
                .build();

        // Tính line total
        orderItem.calculateLineTotal();

        return orderItem;
    }

    /**
     * Generate order number
     */
    public String generateOrderNumber() {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "ORD" + today + String.format("%04d", System.currentTimeMillis() % 10000);
    }

    /**
     * Tính subtotal từ danh sách OrderItem
     */
    public BigDecimal calculateSubtotal(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
