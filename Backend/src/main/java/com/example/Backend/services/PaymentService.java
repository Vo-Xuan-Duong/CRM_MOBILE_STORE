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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PAYMENT_CACHE_PREFIX = "payment:";
    private static final long CACHE_TTL = 1; // 1 hour

    public PaymentResponseDTO createPayment(PaymentCreateDTO createDTO, Long processedById) {
        log.info("Creating payment for order ID: {} with amount: {}",
                createDTO.getOrderId(), createDTO.getAmount());

        // Validate order exists
        Order order = orderRepository.findById(createDTO.getOrderId())
                .orElseThrow(() -> new UserException("Order not found with ID: " + createDTO.getOrderId()));

        // Validate customer exists
        Customer customer = customerRepository.findById(createDTO.getCustomerId())
                .orElseThrow(() -> new UserException("Customer not found with ID: " + createDTO.getCustomerId()));

        // Validate processed by user
        User processedBy = userRepository.findById(processedById)
                .orElseThrow(() -> new UserException("User not found with ID: " + processedById));

        // Validate payment amount
        // validate basic amount (loose)
        if (createDTO.getAmount() == null || createDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("Amount must be positive");
        }

        // Generate payment number
        String paymentNumber = generatePaymentNumber();

        Payment payment = Payment.builder()
                .order(order)
                .customer(customer)
                .amount(createDTO.getAmount())
                .method(createDTO.getMethod())
                .status("pending")
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Cache the payment
        cachePayment(savedPayment);

        log.info("Payment created successfully with number: {}", paymentNumber);
        return convertToResponseDTO(savedPayment);
    }

    public PaymentResponseDTO processPayment(Long id, String gatewayResponse) {
        log.info("Processing payment with ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new UserException("Payment not found with ID: " + id));

        if (!"pending".equalsIgnoreCase(payment.getStatus())) {
            throw new UserException("Payment is not in pending status");
        }

        payment.setStatus("processing");

        // Simulate payment processing
        try {
            // Here you would integrate with actual payment gateway
            Thread.sleep(1000); // Simulate processing delay

            payment.setStatus("completed");
            payment.setPaidAt(LocalDateTime.now());

            // Update order payment status
            updateOrderPaymentStatus(payment.getOrder());

        } catch (Exception e) {
            payment.setStatus("failed");
        }

        Payment updatedPayment = paymentRepository.save(payment);

        // Update cache
        cachePayment(updatedPayment);

        log.info("Payment processed successfully with ID: {}", id);
        return convertToResponseDTO(updatedPayment);
    }

    public PaymentResponseDTO refundPayment(Long id, BigDecimal refundAmount, String refundReason) {
        log.info("Processing refund for payment ID: {} with amount: {}", id, refundAmount);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new UserException("Payment not found with ID: " + id));

        if (!"completed".equalsIgnoreCase(payment.getStatus())) {
            throw new UserException("Payment cannot be refunded");
        }

        BigDecimal maxRefundAmount = payment.getRefundableAmount();
        if (refundAmount.compareTo(maxRefundAmount) > 0) {
            throw new UserException("Refund amount exceeds refundable amount");
        }

        BigDecimal currentRefundAmount = payment.getRefundAmount() != null ?
                payment.getRefundAmount() : BigDecimal.ZERO;
        BigDecimal totalRefundAmount = currentRefundAmount.add(refundAmount);

        // Simplified: mark as refunded when equals amount
        if (totalRefundAmount.compareTo(payment.getAmount()) >= 0) {
            payment.setStatus("refunded");
        }

        // Update status based on refund amount
        if (totalRefundAmount.compareTo(payment.getAmount()) == 0) {
        }

        Payment updatedPayment = paymentRepository.save(payment);

        // Update cache
        cachePayment(updatedPayment);

        log.info("Refund processed successfully for payment ID: {}", id);
        return convertToResponseDTO(updatedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        // Try to get from cache first
        PaymentResponseDTO cachedPayment = getCachedPayment(id);
        if (cachedPayment != null) {
            return cachedPayment;
        }

        Payment payment = paymentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new UserException("Payment not found with ID: " + id));

        PaymentResponseDTO responseDTO = convertToResponseDTO(payment);

        // Cache the result
        cachePaymentResponse(id, responseDTO);

        return responseDTO;
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> searchPayments(String keyword, Pageable pageable) {
        Page<Payment> payments = paymentRepository.searchPayments(keyword, pageable);
        return payments.map(this::convertToResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByCustomer(Long customerId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByCustomerId(customerId, pageable);
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
    public BigDecimal getTotalPaymentsByMethodAndDateRange(String method,
                                                          LocalDateTime startDate,
                                                          LocalDateTime endDate) {
        BigDecimal total = paymentRepository.getTotalPaymentsByMethodAndDateRange(method, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    private void updateOrderPaymentStatus(Order order) {
        // Get total paid amount for this order
        List<Payment> completedPayments = paymentRepository.findByOrderId(order.getId())
                .stream()
                .filter(p -> "completed".equalsIgnoreCase(p.getStatus()))
                .toList();

        BigDecimal totalPaid = completedPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Update order payment status
        if (totalPaid.compareTo(order.getTotal()) >= 0) {
            order.setStatus("paid");
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            order.setStatus("unpaid");
        }

        orderRepository.save(order);
    }

    private String generatePaymentNumber() {
        String prefix = "PAY";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String randomPart = String.format("%04d", (int) (Math.random() * 10000));

        String paymentNumber;
        do {
            paymentNumber = prefix + datePart + randomPart;
            randomPart = String.format("%04d", (int) (Math.random() * 10000));
        } while (paymentRepository.existsByPaymentNumber(paymentNumber));

        return paymentNumber;
    }

    private PaymentResponseDTO convertToResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                
                .orderId(payment.getOrder().getId())
                .orderNumber(payment.getOrder().getOrderNo())
                .customerId(payment.getCustomer().getId())
                .customerName(payment.getCustomer().getFullName())
                .customerPhone(payment.getCustomer().getPhone())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                
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
