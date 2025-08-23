package com.example.Backend.mappers;

import com.example.Backend.dtos.salesorder.SalesOrderItemRequest;
import com.example.Backend.dtos.salesorder.SalesOrderRequest;
import com.example.Backend.dtos.salesorder.SalesOrderResponse;
import com.example.Backend.models.SalesOrder;
import com.example.Backend.models.SalesOrderItem;
import com.example.Backend.models.Customer;
import com.example.Backend.models.SKU;
import com.example.Backend.repositorys.CustomerRepository;
import com.example.Backend.repositorys.SKURepository;
import com.example.Backend.repositorys.SalesOrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SalesOrderMapper {

    private final CustomerRepository customerRepository;
    private final SKURepository skuRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final SalesOrderItemMapper salesOrderItemMapper;

    public SalesOrder toEntity(SalesOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return SalesOrder.builder()
                .customer(customer)
                .notes(request.getNotes())
                .paymentMethod(request.getPaymentMethod())
                .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                .status(SalesOrder.OrderStatus.DRAFT)
                .subtotal(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();
    }

    public SalesOrderResponse toResponse(SalesOrder salesOrder) {
        // Get order items
        List<SalesOrderItem> orderItems = salesOrderItemRepository.findByOrder_Id(salesOrder.getId());

        return SalesOrderResponse.builder()
                .id(salesOrder.getId())
                .customerId(salesOrder.getCustomer() != null ? salesOrder.getCustomer().getId() : null)
                .customerName(salesOrder.getCustomer() != null ? salesOrder.getCustomer().getFullName() : null)
                .userId(salesOrder.getUser() != null ? salesOrder.getUser().getId() : null)
                .userName(salesOrder.getUser() != null ? salesOrder.getUser().getFullName() : null)
                .status(salesOrder.getStatus())
                .paymentMethod(salesOrder.getPaymentMethod())
                .subtotal(salesOrder.getSubtotal())
                .taxAmount(salesOrder.getTaxAmount())
                .discount(salesOrder.getDiscount())
                .total(salesOrder.getTotal())
                .notes(salesOrder.getNotes())
                .items(orderItems.stream()
                        .map(salesOrderItemMapper::toResponse)
                        .collect(Collectors.toList()))
                .createdAt(salesOrder.getCreatedAt())
                .updatedAt(salesOrder.getUpdatedAt())
                .build();
    }

    private SalesOrderItem toOrderItem(SalesOrderItemRequest request, SalesOrder salesOrder) {
        SKU sku = skuRepository.findById(request.getSkuId())
                .orElseThrow(() -> new RuntimeException("SKU not found"));

        SalesOrderItem item = SalesOrderItem.builder()
                .order(salesOrder)
                .sku(sku)
                .quantity(request.getQuantity())
                .unitPrice(sku.getPrice())
                .build();

        item.updateLineTotal();
        return item;
    }
}
