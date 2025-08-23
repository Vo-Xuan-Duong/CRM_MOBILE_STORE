package com.example.Backend.repositorys;

import com.example.Backend.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm theo username
    Optional<User> findByUsername(String username);

    // Tìm theo email
    Optional<User> findByEmail(String email);

    // Tìm theo username hoặc email
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Kiểm tra username đã tồn tại
    boolean existsByUsername(String username);

    // Kiểm tra email đã tồn tại
    boolean existsByEmail(String email);

    // Tìm user active
    List<User> findByIsActiveTrue();

    // Tìm user active với phân trang
    Page<User> findByIsActiveTrue(Pageable pageable);

    // Tìm user active theo tìm kiếm
    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
            "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findActiveUsersWithSearch(@Param("search") String search, Pageable pageable);

    // Tìm user theo role code và active
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.code = :roleCode AND u.isActive = true")
    List<User> findByRoleCodeAndIsActiveTrue(@Param("roleCode") String roleCode);

    // Cập nhật thời gian đăng nhập cuối
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.username = :username")
    void updateLastLoginTime(@Param("username") String username, @Param("loginTime") LocalDateTime loginTime);

    // Đếm số user active
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();

    // Tìm user đã đăng nhập gần đây
    @Query("SELECT u FROM User u WHERE u.lastLoginAt >= :since")
    List<User> findUsersLoggedInSince(@Param("since") LocalDateTime since);

    // Tìm user theo full name
    List<User> findByFullNameContainingIgnoreCase(String fullName);

    // Tìm user theo phone
    Optional<User> findByPhone(String phone);

    long countByIsActiveTrue();

    List<User> findByIsActiveFalse();
}
