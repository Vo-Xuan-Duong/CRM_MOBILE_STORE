package com.example.Backend.repositorys;

import com.example.Backend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Tìm role theo role code
    Optional<Role> findByCode(String code);

    // Kiểm tra role code đã tồn tại
    boolean existsByCode(String code);

    // Tìm roles theo permission code
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.code = :permissionCode")
    List<Role> findByPermissions_Code(@Param("permissionCode") String permissionCode);

    // Tìm roles theo tên role (case insensitive)
    List<Role> findByNameContainingIgnoreCase(String name);

    // Tìm roles theo isActive
    List<Role> findByIsActive(Boolean isActive);
}
