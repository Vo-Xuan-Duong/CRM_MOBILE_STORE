package com.example.Backend.repositorys;

import com.example.Backend.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByCustomerId(Long customerId);

    List<Payment> findByStatus(String status);
    
    List<Payment> findByMethod(String method);

    @Query("SELECT p FROM Payment p WHERE p.customer.id = :customerId")
    Page<Payment> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId")
    Page<Payment> findByOrderId(@Param("orderId") Long orderId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.status = :status")
    Page<Payment> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.method = :method")
    Page<Payment> findByMethod(@Param("method") String method, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.paidAt BETWEEN :startDate AND :endDate")
    List<Payment> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE LOWER(p.status) = 'completed' AND p.paidAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalPaymentsByDateRange(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE LOWER(p.status) = 'completed' AND p.method = :method AND p.paidAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalPaymentsByMethodAndDateRange(@Param("method") String method,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Payment p WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "p.paymentNumber LIKE CONCAT('%', :keyword, '%') OR " +
           "p.transactionId LIKE CONCAT('%', :keyword, '%') OR " +
           "p.referenceNumber LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(p.customer.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.customer.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "p.customer.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<Payment> searchPayments(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") String status);

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.order LEFT JOIN FETCH p.customer WHERE p.id = :id")
    Optional<Payment> findByIdWithDetails(@Param("id") Long id);

    
}
