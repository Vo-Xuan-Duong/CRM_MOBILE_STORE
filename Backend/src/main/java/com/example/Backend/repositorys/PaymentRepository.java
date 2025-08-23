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

    Page<Payment> findByOrder_Customer_Id(@Param("customerId") Long customerId, Pageable pageable);

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

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") String status);

//    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.order LEFT JOIN FETCH p.customer WHERE p.id = :id")
//    Optional<Payment> findByIdWithDetails(@Param("id") Long id);

    
}
