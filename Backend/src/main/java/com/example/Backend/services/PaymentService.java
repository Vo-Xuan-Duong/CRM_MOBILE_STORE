package com.example.Backend.services;

import com.example.Backend.dtos.payment.PaymentCreateDTO;
import com.example.Backend.dtos.payment.PaymentResponseDTO;
import com.example.Backend.exceptions.UserException;
import com.example.Backend.models.*;
import com.example.Backend.repositorys.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PAYMENT_CACHE_PREFIX = "payment:";
    private static final long CACHE_TTL = 1; // 1 hour

    public PaymentResponseDTO createPayment(PaymentCreateDTO createDTO) {
        log.info("Creating payment for order ID: {} with amount: {}",
                createDTO.getOrderId(), createDTO.getAmount());

        // Validate order exists
        SalesOrder order = salesOrderRepository.findById(createDTO.getOrderId())
                .orElseThrow(() -> new UserException("Order not found with ID: " + createDTO.getOrderId()));

        // Validate payment amount
        if (createDTO.getAmount() == null || createDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("Amount must be positive");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(createDTO.getAmount())
                .method(createDTO.getMethod())
                .status(Payment.PaymentStatus.PENDING)
                .paidAt(createDTO.getPaidAt() != null ? createDTO.getPaidAt() : LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Cache the payment
        cachePayment(savedPayment);

        return convertToResponseDTO(savedPayment);
    }

    public PaymentResponseDTO processPayment(Long id) {
        log.info("Processing payment with ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new UserException("Payment not found with ID: " + id));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new UserException("Payment is not in pending status");
        }

        // Simulate payment processing
        try {
            // Here you would integrate with actual payment gateway
            Thread.sleep(1000); // Simulate processing delay

            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());

            // Update order payment status
            updateOrderPaymentStatus(payment.getOrder());

        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            log.error("Payment processing failed for ID: {}", id, e);
        }

        Payment updatedPayment = paymentRepository.save(payment);

        // Update cache
        cachePayment(updatedPayment);

        log.info("Payment processed successfully with ID: {}", id);
        return convertToResponseDTO(updatedPayment);
    }

    public PaymentResponseDTO refundPayment(Long id, BigDecimal refundAmount) {
        log.info("Processing refund for payment ID: {} with amount: {}", id, refundAmount);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new UserException("Payment not found with ID: " + id));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new UserException("Payment must be completed to process refund");
        }

        // Validate refund amount
        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new UserException("Refund amount cannot exceed payment amount");
        }

        // Create refund payment record
        Payment refundPayment = Payment.builder()
                .order(payment.getOrder())
                .amount(refundAmount.negate()) // Negative amount for refund
                .method(payment.getMethod())
                .status(Payment.PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .notes("Refund for payment ID: " + id)
                .build();

        Payment savedRefund = paymentRepository.save(refundPayment);

        // Update order payment status
        updateOrderPaymentStatus(payment.getOrder());

        // Update cache
        cachePayment(savedRefund);

        log.info("Refund processed successfully for payment ID: {}", id);
        return convertToResponseDTO(savedRefund);
    }

    public void cancelPayment(Long id) {
        log.info("Cancelling payment with ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new UserException("Payment not found with ID: " + id));

        if (!payment.canCancel()) {
            throw new UserException("Payment cannot be cancelled in current status: " + payment.getStatus());
        }

        payment.setStatus(Payment.PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        // Update cache
        cachePayment(payment);

        log.info("Payment cancelled successfully with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        // Try to get from cache first
        PaymentResponseDTO cachedPayment = getCachedPayment(id);
        if (cachedPayment != null) {
            return cachedPayment;
        }

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new UserException("Payment not found with ID: " + id));

        PaymentResponseDTO responseDTO = convertToResponseDTO(payment);

        // Cache the result
        cachePaymentResponse(id, responseDTO);

        return responseDTO;
    }



    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByCustomer(Long customerId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByOrder_Customer_Id(customerId, pageable);
        return payments.map(this::convertToResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByOrder(Long orderId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByOrderId(orderId, pageable);
        return payments.map(this::convertToResponseDTO);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = paymentRepository.getTotalPaymentsByDateRange(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaymentsByMethodAndDateRange(Payment.PaymentMethod method,
                                                          LocalDateTime startDate,
                                                          LocalDateTime endDate) {
        BigDecimal total = paymentRepository.getTotalPaymentsByMethodAndDateRange(method.getValue(), startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    private void updateOrderPaymentStatus(SalesOrder order) {
        // Get total paid amount for this order
        List<Payment> completedPayments = paymentRepository.findByOrderId(order.getId())
                .stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .toList();

        BigDecimal totalPaid = completedPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Update order payment status based on total paid amount
        if (totalPaid.compareTo(order.getTotal()) >= 0) {
            order.setStatus(SalesOrder.OrderStatus.PAID);
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            // Set to CONFIRMED if partially paid
            order.setStatus(SalesOrder.OrderStatus.CONFIRMED);
        }

        salesOrderRepository.save(order);
    }

    private PaymentResponseDTO convertToResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .orderNo(payment.getOrder().getId().toString()) // Use order ID as orderNo since getOrderNumber() doesn't exist
                .method(payment.getMethod().getValue())
                .status(payment.getStatus().getValue())
                .amount(payment.getAmount())
                .paidAt(payment.getPaidAt())
                .customer(PaymentResponseDTO.CustomerInfo.builder()
                        .id(payment.getOrder().getCustomer().getId())
                        .fullName(payment.getOrder().getCustomer().getFullName())
                        .phone(payment.getOrder().getCustomer().getPhone())
                        .build())
                .build();
    }

    // Cache methods
    private void cachePayment(Payment payment) {
        String key = PAYMENT_CACHE_PREFIX + payment.getId();
        redisTemplate.opsForValue().set(key, convertToResponseDTO(payment), CACHE_TTL, TimeUnit.HOURS);
    }

    private void cachePaymentResponse(Long id, PaymentResponseDTO responseDTO) {
        String key = PAYMENT_CACHE_PREFIX + id;
        redisTemplate.opsForValue().set(key, responseDTO, CACHE_TTL, TimeUnit.HOURS);
    }

    private PaymentResponseDTO getCachedPayment(Long id) {
        String key = PAYMENT_CACHE_PREFIX + id;
        return (PaymentResponseDTO) redisTemplate.opsForValue().get(key);
    }
}
