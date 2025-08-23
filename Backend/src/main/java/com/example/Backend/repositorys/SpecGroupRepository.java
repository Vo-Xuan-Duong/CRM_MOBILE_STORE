package com.example.Backend.repositorys;

import com.example.Backend.models.SpecGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecGroupRepository extends JpaRepository<SpecGroup, Long> {

    // Tìm kiếm cơ bản
    Optional<SpecGroup> findByName(String name);
    Optional<SpecGroup> findByNameAndIsActiveTrue(String name);

    // Tìm kiếm theo trạng thái
    List<SpecGroup> findByIsActiveTrueOrderBySortOrder();
    Page<SpecGroup> findByIsActiveTrue(Pageable pageable);
    List<SpecGroup> findByIsActiveFalse();

    // Kiểm tra tồn tại
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);

    // Thống kê
    @Query("SELECT COUNT(sg) FROM SpecGroup sg WHERE sg.isActive = true")
    long countActiveSpecGroups();

    // Tìm kiếm nâng cao
    @Query("SELECT sg FROM SpecGroup sg WHERE " +
           "(:name IS NULL OR LOWER(sg.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "sg.isActive = :isActive " +
           "ORDER BY sg.sortOrder")
    List<SpecGroup> searchSpecGroups(
        @Param("name") String name,
        @Param("isActive") Boolean isActive
    );

    // Lấy theo sort order
    List<SpecGroup> findBySortOrderBetweenAndIsActiveTrue(Integer minOrder, Integer maxOrder);
}
