package com.example.Backend.repositorys;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.models.Customer;
import com.example.Backend.models.SalesOrder;
import com.example.Backend.models.SalesOrder.OrderStatus;
import com.example.Backend.models.User;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    Optional<SalesOrder> findByOrderNumber(String orderNumber);

    List<SalesOrder> findByCustomer(Customer customer);

    List<SalesOrder> findByCustomerId(Long customerId);

    List<SalesOrder> findByUser(User user);

    List<SalesOrder> findByUserId(Long userId);

    List<SalesOrder> findByStatus(OrderStatus status);

    Page<SalesOrder> findByStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT so FROM SalesOrder so WHERE so.orderDate BETWEEN :startDate AND :endDate")
    List<SalesOrder> findByOrderDateBetween(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT so FROM SalesOrder so WHERE so.createdAt BETWEEN :startDate AND :endDate")
    List<SalesOrder> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT so FROM SalesOrder so WHERE so.total BETWEEN :minTotal AND :maxTotal")
    List<SalesOrder> findByTotalBetween(@Param("minTotal") BigDecimal minTotal,
                                       @Param("maxTotal") BigDecimal maxTotal);

    @Query("SELECT so FROM SalesOrder so WHERE " +
           "(:orderNumber IS NULL OR so.orderNumber LIKE CONCAT('%', :orderNumber, '%')) AND " +
           "(:customerId IS NULL OR so.customer.id = :customerId) AND " +
           "(:userId IS NULL OR so.user.id = :userId) AND " +
           "(:status IS NULL OR so.status = :status) AND " +
           "(:startDate IS NULL OR so.orderDate >= :startDate) AND " +
           "(:endDate IS NULL OR so.orderDate <= :endDate)")
    Page<SalesOrder> findBySearchCriteria(@Param("orderNumber") String orderNumber,
                                         @Param("customerId") Long customerId,
                                         @Param("userId") Long userId,
                                         @Param("status") OrderStatus status,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         Pageable pageable);

    @Query("SELECT SUM(so.total) FROM SalesOrder so WHERE so.status = 'PAID' AND so.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalSalesBetween(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(so) FROM SalesOrder so WHERE so.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT so.status, COUNT(so) FROM SalesOrder so GROUP BY so.status")
    List<Object[]> getOrderCountByStatus();

    @Query("SELECT DATE(so.orderDate), COUNT(so), SUM(so.total) FROM SalesOrder so " +
           "WHERE so.status = 'PAID' AND so.orderDate BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(so.orderDate) ORDER BY DATE(so.orderDate)")
    List<Object[]> getDailySalesReport(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT u.fullName, COUNT(so), SUM(so.total) FROM SalesOrder so JOIN so.user u " +
           "WHERE so.status = 'PAID' AND so.orderDate BETWEEN :startDate AND :endDate " +
           "GROUP BY u.id, u.fullName ORDER BY SUM(so.total) DESC")
    List<Object[]> getSalesPerformanceByUser(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    boolean existsByOrderNumber(String orderNumber);
}
