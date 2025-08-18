package com.example.Backend.repositorys;

import com.example.Backend.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // Tìm permission theo code
    Optional<Permission> findByCode(String code);

    // Kiểm tra permission code đã tồn tại
    boolean existsByCode(String code);

    // Tìm permissions theo name (case insensitive)
    List<Permission> findByNameContainingIgnoreCase(String name);

    // Tìm permissions theo code (case insensitive)
    List<Permission> findByCodeContainingIgnoreCase(String code);
}