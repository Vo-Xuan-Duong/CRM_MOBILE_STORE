package com.example.Backend.repositorys;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.models.Warranty;
import com.example.Backend.models.Warranty.WarrantyStatus;

@Repository
public interface WarrantyRepository extends JpaRepository<Warranty, Long> {

    Optional<Warranty> findByWarrantyCode(String warrantyCode);

    List<Warranty> findByCustomerId(Long customerId);

    List<Warranty> findByStatus(WarrantyStatus status);

    @Query("SELECT w FROM Warranty w WHERE w.endDate <= :cutoffDate AND w.status = 'ACTIVE'")
    List<Warranty> findExpiringWarranties(@Param("cutoffDate") LocalDate cutoffDate);

    @Query("SELECT w FROM Warranty w WHERE w.endDate < :today AND w.status = 'ACTIVE'")
    List<Warranty> findExpiredWarranties(@Param("today") LocalDate today);

    @Query("SELECT w FROM Warranty w WHERE w.status = 'ACTIVE' AND w.endDate >= CURRENT_DATE")
    List<Warranty> findActiveWarranties();

    @Query("SELECT w FROM Warranty w WHERE " +
           "LOWER(w.warrantyCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(w.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(w.serialUnit.imei) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Warranty> searchWarranties(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT w FROM Warranty w WHERE w.startDate BETWEEN :startDate AND :endDate")
    List<Warranty> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    long countByStatus(WarrantyStatus status);

    @Query("SELECT COUNT(w) FROM Warranty w WHERE w.endDate < :date")
    long countExpiredWarranties(@Param("date") LocalDate date);

    @Query("SELECT COUNT(w) FROM Warranty w WHERE w.endDate <= :date AND w.status = 'ACTIVE'")
    long countExpiringWithinDays(@Param("date") LocalDate date);
}
