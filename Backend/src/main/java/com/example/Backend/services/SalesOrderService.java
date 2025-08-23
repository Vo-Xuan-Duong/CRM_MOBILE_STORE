package com.example.Backend.services;

import com.example.Backend.dtos.salesorder.SalesOrderRequest;
import com.example.Backend.dtos.salesorder.SalesOrderResponse;
import com.example.Backend.dtos.salesorder.SalesOrderItemRequest;
import com.example.Backend.exceptions.OrderException;
import com.example.Backend.exceptions.StockException;
import com.example.Backend.mappers.SalesOrderMapper;
import com.example.Backend.models.*;
import com.example.Backend.models.SalesOrder.OrderStatus;
import com.example.Backend.repositorys.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final CustomerRepository customerRepository;
    private final SKURepository skuRepository;
    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final AuthService authService;

    public SalesOrderResponse createOrder(SalesOrderRequest request) {
        try {
            Customer customer = findCustomerById(request.getCustomerId());
            User user = authService.getCurrentUser();

            SalesOrder order = SalesOrder.builder()
                    .customer(customer)
                    .user(user)
                    .status(OrderStatus.DRAFT)
                    .paymentMethod(request.getPaymentMethod())
                    .subtotal(BigDecimal.ZERO)
                    .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                    .total(BigDecimal.ZERO)
                    .notes(request.getNotes())
                    .build();

            SalesOrder savedOrder = salesOrderRepository.save(order);

            // Add order items if provided
            if (request.getItems() != null && !request.getItems().isEmpty()) {

                addItemsToOrder(savedOrder, request.getItems());

                calculateOrderTotals(savedOrder);

                savedOrder = salesOrderRepository.save(savedOrder);
            }

            return salesOrderMapper.toResponse(savedOrder);
        } catch (Exception e) {
            log.error("Error creating sales order: {}", e.getMessage());
            throw e;
        }
    }

    public SalesOrderResponse updateOrder(Long id, SalesOrderRequest request) {
        try {
            log.info("Updating sales order ID: {}", id);

            SalesOrder order = findOrderById(id);

            if (!order.isCancellable()) {
                throw new OrderException("Order cannot be modified in current status: " + order.getStatus());
            }

            // Update basic order information
            order.setPaymentMethod(request.getPaymentMethod());
            order.setDiscount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO);
            order.setNotes(request.getNotes());

            // Update order items if provided
            if (request.getItems() != null) {
                updateOrderItems(order, request.getItems());
            }

            // Recalculate totals after items update
            calculateOrderTotals(order);

            SalesOrder savedOrder = salesOrderRepository.save(order);
            log.info("Successfully updated sales order ID: {}", id);
            return salesOrderMapper.toResponse(savedOrder);
        } catch (Exception e) {
            log.error("Error updating sales order ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    public SalesOrderResponse getOrderById(Long id) {
        SalesOrder order = findOrderById(id);
        return salesOrderMapper.toResponse(order);
    }

    public Page<SalesOrderResponse> getAllOrders(Pageable pageable) {
        return salesOrderRepository.findAll(pageable)
                .map(salesOrderMapper::toResponse);
    }

    public Page<SalesOrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return salesOrderRepository.findByStatus(status, pageable)
                .map(salesOrderMapper::toResponse);
    }

    public List<SalesOrderResponse> getOrdersByCustomer(Long customerId) {
        return salesOrderRepository.findByCustomerId(customerId).stream()
                .map(salesOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<SalesOrderResponse> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        return salesOrderRepository.findByOrderDateBetween(startDate, endDate).stream()
                .map(salesOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SalesOrderResponse confirmOrder(Long id) {
        SalesOrder order = findOrderById(id);

        if (order.getStatus() != OrderStatus.DRAFT && order.getStatus() != OrderStatus.PENDING) {
            throw new OrderException("Order cannot be confirmed in current status: " + order.getStatus());
        }

        // Reserve stock for all items
        reserveStockForOrder(order);

        order.setStatus(OrderStatus.CONFIRMED);
        SalesOrder savedOrder = salesOrderRepository.save(order);

        return salesOrderMapper.toResponse(savedOrder);
    }

    public SalesOrderResponse payOrder(Long id) {
        SalesOrder order = findOrderById(id);

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new OrderException("Order must be confirmed before payment");
        }

        // Process stock movements
        processStockMovements(order);

        order.setStatus(OrderStatus.PAID);
        SalesOrder savedOrder = salesOrderRepository.save(order);

        return salesOrderMapper.toResponse(savedOrder);
    }

    public void cancelOrder(Long id) {
        SalesOrder order = findOrderById(id);

        if (!order.isCancellable()) {
            throw new OrderException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        // Release reserved stock
        releaseReservedStockForOrder(order);

        order.setStatus(OrderStatus.CANCELLED);
        salesOrderRepository.save(order);
    }

    public BigDecimal getTotalSales(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = salesOrderRepository.getTotalSalesBetween(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    public long getOrderCountByStatus(OrderStatus status) {
        return salesOrderRepository.countByStatus(status);
    }

    public List<Object[]> getOrderStatistics() {
        return salesOrderRepository.getOrderCountByStatus();
    }

    public List<Object[]> getDailySalesReport(LocalDate startDate, LocalDate endDate) {
        return salesOrderRepository.getDailySalesReport(startDate, endDate);
    }

    public List<Object[]> getSalesPerformanceByUser(LocalDate startDate, LocalDate endDate) {
        return salesOrderRepository.getSalesPerformanceByUser(startDate, endDate);
    }

    private void addItemsToOrder(SalesOrder order, List<SalesOrderItemRequest> itemRequests) {
        for (SalesOrderItemRequest itemRequest : itemRequests) {
            SKU sku = findSkuById(itemRequest.getSkuId());

            // Check stock availability
            if (!checkStockAvailability(sku.getId(), itemRequest.getQuantity())) {
                throw new StockException("Insufficient stock for SKU: " + sku.getId());
            }

            SalesOrderItem item = SalesOrderItem.builder()
                    .order(order)
                    .sku(sku)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(sku.getPrice())
                    .build();

            item.updateLineTotal();

        }
    }

    private void updateOrderItems(SalesOrder order, List<SalesOrderItemRequest> itemRequests) {
        // Get all existing items for this order
        List<SalesOrderItem> existingItems = getOrderItems(order.getId());

        // Clear existing items
        for (SalesOrderItem existingItem : existingItems) {
            salesOrderItemRepository.delete(existingItem);
        }

        // Add new items
        if (itemRequests != null && !itemRequests.isEmpty()) {
            for (SalesOrderItemRequest itemRequest : itemRequests) {
                SKU sku = findSkuById(itemRequest.getSkuId());

                // Check stock availability
                if (!checkStockAvailability(sku.getId(), itemRequest.getQuantity())) {
                    throw new StockException("Insufficient stock for SKU: " + sku.getId());
                }

                SalesOrderItem item = SalesOrderItem.builder()
                        .order(order)
                        .sku(sku)
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(sku.getPrice())
                        .build();

                item.updateLineTotal();
                salesOrderItemRepository.save(item);
            }
        }
    }

    private void calculateOrderTotals(SalesOrder order) {
        try {
            List<SalesOrderItem> items = getOrderItems(order.getId());

            BigDecimal subtotal = items.stream()
                    .map(SalesOrderItem::getLineTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // Fix ambiguous reference

            order.setSubtotal(subtotal);

            // Calculate tax (assuming 10% VAT)
            BigDecimal taxAmount = subtotal.multiply(new BigDecimal("0.10"));
            order.setTaxAmount(taxAmount);

            // Calculate total
            BigDecimal total = subtotal.add(taxAmount).subtract(order.getDiscount());
            order.setTotal(total);
        } catch (Exception e) {
            log.error("Error calculating order totals for order ID {}: {}", order.getId(), e.getMessage());

            order.setSubtotal(BigDecimal.ZERO);
            order.setTaxAmount(BigDecimal.ZERO);
            order.setTotal(BigDecimal.ZERO);
        }
    }

    private void reserveStockForOrder(SalesOrder order) {
        List<SalesOrderItem> items = getOrderItems(order.getId());
        for (SalesOrderItem item : items) {
            int reserved = stockItemRepository.reserveStock(item.getSku().getId(), item.getQuantity());
            if (reserved == 0) {
                throw new StockException("Failed to reserve stock for SKU: " + item.getSku().getId());
            }
        }
    }

    private void releaseReservedStockForOrder(SalesOrder order) {
        List<SalesOrderItem> items = getOrderItems(order.getId());
        for (SalesOrderItem item : items) {
            stockItemRepository.releaseReservation(item.getSku().getId(), item.getQuantity());
        }
    }

    private void processStockMovements(SalesOrder order) {
        List<SalesOrderItem> items = getOrderItems(order.getId());
        for (SalesOrderItem item : items) {
            // Release reservation
            stockItemRepository.releaseReservation(item.getSku().getId(), item.getQuantity());

            // Decrease actual stock
            int decreased = stockItemRepository.decreaseStock(item.getSku().getId(), item.getQuantity());
            if (decreased == 0) {
                throw new StockException("Failed to decrease stock for SKU: " + item.getSku().getId());
            }

            // Create stock movement record
            StockMovement movement = StockMovement.builder()
                    .sku(item.getSku())
                    .movementType(StockMovement.MovementType.OUT)
                    .quantity(item.getQuantity())
                    .reason(StockMovement.MovementReason.SALE)
                    .refType("sales_order")
                    .refId(order.getId())
                    .createdBy(order.getUser())
                    .build();

            stockMovementRepository.save(movement);
        }
    }

    private List<SalesOrderItem> getOrderItems(Long orderId) {
        List<SalesOrderItem> items = salesOrderItemRepository.findByOrder_Id(orderId);
        if (items == null || items.isEmpty()) {
            log.warn("No items found for order ID: {}", orderId);
            return new ArrayList<>();
        }
        return items;
    }

    private boolean checkStockAvailability(Long skuId, Integer requiredQuantity) {
        return stockItemRepository.findBySkuId(skuId)
                .map(stockItem -> stockItem.canReserve(requiredQuantity))
                .orElse(false);
    }

    private SalesOrder findOrderById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found with id: " + id));
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new OrderException("Customer not found with id: " + id));
    }

    private SKU findSkuById(Long id) {
        return skuRepository.findById(id)
                .orElseThrow(() -> new OrderException("SKU not found with id: " + id));
    }
}
