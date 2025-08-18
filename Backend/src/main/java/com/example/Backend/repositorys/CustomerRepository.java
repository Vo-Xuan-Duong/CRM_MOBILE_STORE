package com.example.Backend.repositorys;

import com.example.Backend.models.Customer;
import com.example.Backend.models.Customer.CustomerTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Tìm theo phone
    Optional<Customer> findByPhone(String phone);

    // Tìm theo email
    Optional<Customer> findByEmail(String email);

    // Kiểm tra phone đã tồn tại
    boolean existsByPhone(String phone);

    // Kiểm tra email đã tồn tại
    boolean existsByEmail(String email);

    // Tìm theo tên (case insensitive)
    List<Customer> findByFullNameContainingIgnoreCase(String fullName);

    // Tìm theo gender
    List<Customer> findByGender(Customer.Gender gender);

    // Tìm theo thành phố
    List<Customer> findByCityContainingIgnoreCase(String city);

    // Tìm theo tỉnh
    List<Customer> findByProvinceContainingIgnoreCase(String province);

    // Tìm theo khoảng ngày sinh
    @Query("SELECT c FROM Customer c WHERE c.birthDate BETWEEN :fromDate AND :toDate")
    List<Customer> findByBirthDateBetween(@Param("fromDate") LocalDate fromDate,
                                        @Param("toDate") LocalDate toDate);

    // Tìm theo khoảng ngày tạo
    @Query("SELECT c FROM Customer c WHERE c.createdAt BETWEEN :fromDate AND :toDate")
    List<Customer> findByCreatedAtBetween(@Param("fromDate") LocalDateTime fromDate,
                                        @Param("toDate") LocalDateTime toDate);

    // Tìm kiếm phức tạp
    @Query("SELECT c FROM Customer c WHERE " +
           "(:fullName IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND " +
           "(:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:gender IS NULL OR c.gender = :gender) AND " +
           "(:city IS NULL OR LOWER(c.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:province IS NULL OR LOWER(c.province) LIKE LOWER(CONCAT('%', :province, '%')))")
    Page<Customer> findBySearchCriteria(@Param("fullName") String fullName,
                                      @Param("phone") String phone,
                                      @Param("email") String email,
                                      @Param("gender") Customer.Gender gender,
                                      @Param("city") String city,
                                      @Param("province") String province,
                                      Pageable pageable);

    // Thống kê khách hàng theo tháng
    @Query("SELECT MONTH(c.createdAt), COUNT(c) FROM Customer c " +
           "WHERE YEAR(c.createdAt) = :year GROUP BY MONTH(c.createdAt)")
    List<Object[]> getCustomerCountByMonth(@Param("year") int year);

    // Thống kê khách hàng theo giới tính
    @Query("SELECT c.gender, COUNT(c) FROM Customer c GROUP BY c.gender")
    List<Object[]> getCustomerCountByGender();

    // Thống kê khách hàng theo tỉnh thành
    @Query("SELECT c.province, COUNT(c) FROM Customer c " +
           "WHERE c.province IS NOT NULL GROUP BY c.province ORDER BY COUNT(c) DESC")
    List<Object[]> getCustomerCountByProvince();

    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    List<Customer> findByIsActiveTrue();

    Page<Customer> findByIsActiveTrue(Pageable pageable);

    List<Customer> findByTier(CustomerTier tier);

    Page<Customer> findByTier(CustomerTier tier, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.isActive = true AND " +
           "(LOWER(c.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "c.phone LIKE CONCAT('%', :search, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Customer> findActiveCustomersWithSearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.tier = :tier AND c.isActive = true")
    List<Customer> findByTierAndIsActiveTrue(@Param("tier") CustomerTier tier);

    @Query("SELECT c FROM Customer c WHERE c.dob BETWEEN :startDate AND :endDate")
    List<Customer> findCustomersByBirthdayRange(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.tier = :tier AND c.isActive = true")
    long countByTierAndIsActiveTrue(@Param("tier") CustomerTier tier);

    @Query("SELECT c.tier, COUNT(c) FROM Customer c WHERE c.isActive = true GROUP BY c.tier")
    List<Object[]> getCustomerTierStatistics();

    @Query("SELECT c FROM Customer c WHERE MONTH(c.dob) = :month AND DAY(c.dob) = :day")
    List<Customer> findCustomersWithBirthdayToday(@Param("month") int month, @Param("day") int day);
}
