package com.example.Backend.repositorys;

import com.example.Backend.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    // Tìm kiếm cơ bản
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneAndIsActiveTrue(String phone);

    // Tìm kiếm theo trạng thái
    Page<Customer> findByIsActiveTrue(Pageable pageable);
    Page<Customer> findByIsActiveFalse(Pageable pageable);

    // Tìm kiếm theo tier
    List<Customer> findByTier(Customer.CustomerTier tier);

    // Tìm kiếm theo thời gian
    List<Customer> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Kiểm tra tồn tại
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhoneAndIdNot(String phone, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    // Thống kê
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.isActive = true")
    long countActiveCustomers();

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.tier = :tier")
    long countByTier(@Param("tier") Customer.CustomerTier tier);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt >= :date")
    long countCustomersCreatedAfter(@Param("date") LocalDateTime date);

    // Tìm kiếm nâng cao
    @Query("SELECT c FROM Customer c WHERE " +
           "(:fullName IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND " +
           "(:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:gender IS NULL OR c.gender = :gender) AND " +
           "(:tier IS NULL OR c.tier = :tier) AND " +
           "c.isActive = :isActive")
    Page<Customer> searchCustomers(
        @Param("fullName") String fullName,
        @Param("phone") String phone,
        @Param("email") String email,
        @Param("gender") Customer.Gender gender,
        @Param("tier") Customer.CustomerTier tier,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );
}
