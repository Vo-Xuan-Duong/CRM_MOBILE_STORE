package com.example.Backend.repositorys;

import com.example.Backend.models.SpecField;
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
public interface SpecFieldRepository extends JpaRepository<SpecField, Long> {

    // Tìm kiếm cơ bản
    Optional<SpecField> findByGroupAndFieldKey(SpecGroup group, String fieldKey);
    Optional<SpecField> findByGroupIdAndFieldKey(Long groupId, String fieldKey);

    // Tìm kiếm theo group
    List<SpecField> findByGroupAndIsActiveTrueOrderBySortOrder(SpecGroup group);
    List<SpecField> findByGroupIdAndIsActiveTrueOrderBySortOrder(Long groupId);
    Page<SpecField> findByGroupId(Long groupId, Pageable pageable);

    // Tìm kiếm theo trạng thái
    Page<SpecField> findByIsActiveTrue(Pageable pageable);
    Page<SpecField> findByIsActiveFalse(Pageable pageable);

    // Tìm kiếm theo applies to
    List<SpecField> findByAppliesToAndIsActiveTrueOrderByGroupIdAscSortOrderAsc(SpecField.AppliesTo appliesTo);
    List<SpecField> findByGroupIdAndAppliesToAndIsActiveTrueOrderBySortOrder(Long groupId, SpecField.AppliesTo appliesTo);

    // Tìm kiếm theo data type
    List<SpecField> findByDataTypeAndIsActiveTrueOrderByGroupIdAscSortOrderAsc(SpecField.DataType dataType);

    // Kiểm tra tồn tại
    boolean existsByGroupAndFieldKey(SpecGroup group, String fieldKey);
    boolean existsByGroupIdAndFieldKey(Long groupId, String fieldKey);
    boolean existsByGroupIdAndFieldKeyAndIdNot(Long groupId, String fieldKey, Long id);

    // Thống kê
    @Query("SELECT COUNT(sf) FROM SpecField sf WHERE sf.isActive = true")
    long countActiveSpecFields();

    @Query("SELECT COUNT(sf) FROM SpecField sf WHERE sf.group.id = :groupId AND sf.isActive = true")
    long countActiveSpecFieldsByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT COUNT(sf) FROM SpecField sf WHERE sf.appliesTo = :appliesTo AND sf.isActive = true")
    long countActiveSpecFieldsByAppliesTo(@Param("appliesTo") SpecField.AppliesTo appliesTo);

    // Tìm kiếm nâng cao
    @Query("SELECT sf FROM SpecField sf WHERE " +
           "(:groupId IS NULL OR sf.group.id = :groupId) AND " +
           "(:fieldKey IS NULL OR LOWER(sf.fieldKey) LIKE LOWER(CONCAT('%', :fieldKey, '%'))) AND " +
           "(:label IS NULL OR LOWER(sf.label) LIKE LOWER(CONCAT('%', :label, '%'))) AND " +
           "(:dataType IS NULL OR sf.dataType = :dataType) AND " +
           "(:appliesTo IS NULL OR sf.appliesTo = :appliesTo) AND " +
           "sf.isActive = :isActive " +
           "ORDER BY sf.group.sortOrder, sf.sortOrder")
    Page<SpecField> searchSpecFields(
        @Param("groupId") Long groupId,
        @Param("fieldKey") String fieldKey,
        @Param("label") String label,
        @Param("dataType") SpecField.DataType dataType,
        @Param("appliesTo") SpecField.AppliesTo appliesTo,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );

    // Lấy tất cả fields cho một group với pagination
    @Query("SELECT sf FROM SpecField sf WHERE sf.group.id = :groupId ORDER BY sf.sortOrder")
    Page<SpecField> findAllByGroupIdOrderBySortOrder(@Param("groupId") Long groupId, Pageable pageable);
}
