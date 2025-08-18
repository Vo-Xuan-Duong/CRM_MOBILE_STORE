package com.example.Backend.services;

import com.example.Backend.dtos.order.SalesOrderRequest;
import com.example.Backend.dtos.order.SalesOrderResponse;
import com.example.Backend.exceptions.OrderException;
import com.example.Backend.exceptions.StockException;
import com.example.Backend.mappers.SalesOrderMapper;
import com.example.Backend.models.*;
import com.example.Backend.models.SalesOrder.OrderStatus;
import com.example.Backend.repositorys.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final SKURepository skuRepository;
    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final SalesOrderMapper salesOrderMapper;

    public SalesOrderResponse createOrder(SalesOrderRequest request, Long userId) {
        Customer customer = findCustomerById(request.getCustomerId());
        User user = findUserById(userId);

        String orderNumber = generateOrderNumber();

        SalesOrder order = SalesOrder.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .user(user)
                .status(OrderStatus.DRAFT)
                .paymentMethod(request.getPaymentMethod())
                .subtotal(BigDecimal.ZERO)
                .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .notes(request.getNotes())
                .orderDate(LocalDate.now())
                .build();

        SalesOrder savedOrder = salesOrderRepository.save(order);

        // Add order items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            addItemsToOrder(savedOrder, request.getItems());
        }

        return salesOrderMapper.toResponse(savedOrder);
    }

    public SalesOrderResponse updateOrder(Long id, SalesOrderRequest request) {
        SalesOrder order = findOrderById(id);

        if (!order.isCancellable()) {
            throw new OrderException("Order cannot be modified in current status: " + order.getStatus());
        }

        order.setPaymentMethod(request.getPaymentMethod());
        order.setDiscount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO);
        order.setNotes(request.getNotes());

        // Recalculate totals
        calculateOrderTotals(order);

        SalesOrder savedOrder = salesOrderRepository.save(order);
        return salesOrderMapper.toResponse(savedOrder);
    }

    public SalesOrderResponse getOrderById(Long id) {
        SalesOrder order = findOrderById(id);
        return salesOrderMapper.toResponse(order);
    }

    public SalesOrderResponse getOrderByNumber(String orderNumber) {
        SalesOrder order = salesOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderException("Order not found with number: " + orderNumber));
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
                    .unitPrice(itemRequest.getUnitPrice() != null ? itemRequest.getUnitPrice() : sku.getPrice())
                    .discount(itemRequest.getDiscount() != null ? itemRequest.getDiscount() : BigDecimal.ZERO)
                    .warrantyMonths(itemRequest.getWarrantyMonths() != null ? itemRequest.getWarrantyMonths() : 12)
                    .build();

            item.updateLineTotal();
            order.getItems().add(item);
        }

        calculateOrderTotals(order);
    }

    private void calculateOrderTotals(SalesOrder order) {
        BigDecimal subtotal = order.getItems().stream()
                .map(SalesOrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setSubtotal(subtotal);

        // Calculate tax (assuming 10% VAT)
        BigDecimal taxAmount = subtotal.multiply(new BigDecimal("0.10"));
        order.setTaxAmount(taxAmount);

        // Calculate total
        BigDecimal total = subtotal.add(taxAmount).subtract(order.getDiscount());
        order.setTotal(total);
    }

    private void reserveStockForOrder(SalesOrder order) {
        for (SalesOrderItem item : order.getItems()) {
            int reserved = stockItemRepository.reserveStock(item.getSku().getId(), item.getQuantity());
            if (reserved == 0) {
                throw new StockException("Failed to reserve stock for SKU: " + item.getSku().getId());
            }
        }
    }

    private void releaseReservedStockForOrder(SalesOrder order) {
        for (SalesOrderItem item : order.getItems()) {
            stockItemRepository.releaseReservation(item.getSku().getId(), item.getQuantity());
        }
    }

    private void processStockMovements(SalesOrder order) {
        for (SalesOrderItem item : order.getItems()) {
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

    private boolean checkStockAvailability(Long skuId, Integer requiredQuantity) {
        return stockItemRepository.findBySkuId(skuId)
                .map(stockItem -> stockItem.canReserve(requiredQuantity))
                .orElse(false);
    }

    private String generateOrderNumber() {
        String prefix = "SO";
        String date = LocalDate.now().toString().replace("-", "");
        long count = salesOrderRepository.count() + 1;
        return String.format("%s%s%06d", prefix, date, count);
    }

    private SalesOrder findOrderById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found with id: " + id));
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new OrderException("Customer not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new OrderException("User not found with id: " + id));
    }

    private SKU findSkuById(Long id) {
        return skuRepository.findById(id)
                .orElseThrow(() -> new OrderException("SKU not found with id: " + id));
    }
}
