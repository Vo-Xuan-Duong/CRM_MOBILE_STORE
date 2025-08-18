package com.example.Backend.repositorys;

import com.example.Backend.models.InstallmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, Long> {

    Optional<InstallmentPlan> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    List<InstallmentPlan> findByStatus(InstallmentPlan.InstallmentStatus status);

    List<InstallmentPlan> findByProvider(String provider);

    @Query("SELECT ip FROM InstallmentPlan ip WHERE ip.order.customer.id = :customerId")
    List<InstallmentPlan> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT ip FROM InstallmentPlan ip WHERE ip.nextPaymentDate <= :date AND ip.status = :status")
    List<InstallmentPlan> findByNextPaymentDateBeforeAndStatus(@Param("date") LocalDate date, @Param("status") InstallmentPlan.InstallmentStatus status);

    @Query("SELECT COUNT(ip) FROM InstallmentPlan ip WHERE ip.status = :status")
    long countByStatus(@Param("status") InstallmentPlan.InstallmentStatus status);

    @Query("SELECT COALESCE(SUM(ip.principal), 0) FROM InstallmentPlan ip")
    BigDecimal sumTotalPrincipal();

    @Query("SELECT COALESCE(SUM(ip.remainingBalance), 0) FROM InstallmentPlan ip WHERE ip.status = 'ACTIVE'")
    BigDecimal sumRemainingBalance();

    @Query("SELECT ip FROM InstallmentPlan ip WHERE ip.remainingBalance > 0 AND ip.status = 'ACTIVE'")
    List<InstallmentPlan> findActiveWithOutstandingBalance();

    @Query("SELECT ip FROM InstallmentPlan ip WHERE ip.nextPaymentDate BETWEEN :startDate AND :endDate AND ip.status = 'ACTIVE'")
    List<InstallmentPlan> findUpcomingPayments(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
