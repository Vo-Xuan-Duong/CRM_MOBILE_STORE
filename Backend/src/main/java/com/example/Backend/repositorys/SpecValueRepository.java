package com.example.Backend.repositorys;

import com.example.Backend.models.SpecValue;
import com.example.Backend.models.SpecField;
import com.example.Backend.models.ProductModel;
import com.example.Backend.models.SKU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecValueRepository extends JpaRepository<SpecValue, Long> {

    // Tìm kiếm cơ bản
    Optional<SpecValue> findByFieldAndProductModel(SpecField field, ProductModel productModel);
    Optional<SpecValue> findByFieldAndSku(SpecField field, SKU sku);
    Optional<SpecValue> findByFieldIdAndProductModelId(Long fieldId, Long productModelId);
    Optional<SpecValue> findByFieldIdAndSkuId(Long fieldId, Long skuId);

    // Tìm kiếm theo ProductModel
    List<SpecValue> findByProductModelOrderByFieldGroupSortOrderAscFieldSortOrderAsc(ProductModel productModel);
    List<SpecValue> findByProductModelIdOrderByFieldGroupSortOrderAscFieldSortOrderAsc(Long productModelId);

    // Tìm kiếm theo SKU
    List<SpecValue> findBySkuOrderByFieldGroupSortOrderAscFieldSortOrderAsc(SKU sku);
    List<SpecValue> findBySkuIdOrderByFieldGroupSortOrderAscFieldSortOrderAsc(Long skuId);

    // Tìm kiếm theo SpecField
    List<SpecValue> findByField(SpecField field);
    List<SpecValue> findByFieldId(Long fieldId);
    Page<SpecValue> findByFieldId(Long fieldId, Pageable pageable);

    // Tìm kiếm theo SpecGroup (thông qua SpecField)
    @Query("SELECT sv FROM SpecValue sv WHERE sv.field.group.id = :groupId")
    List<SpecValue> findByFieldGroupId(@Param("groupId") Long groupId);

    @Query("SELECT sv FROM SpecValue sv WHERE sv.field.group.id = :groupId AND sv.productModel.id = :productModelId")
    List<SpecValue> findByFieldGroupIdAndProductModelId(@Param("groupId") Long groupId, @Param("productModelId") Long productModelId);

    @Query("SELECT sv FROM SpecValue sv WHERE sv.field.group.id = :groupId AND sv.sku.id = :skuId")
    List<SpecValue> findByFieldGroupIdAndSkuId(@Param("groupId") Long groupId, @Param("skuId") Long skuId);

    // Kiểm tra tồn tại
    boolean existsByFieldAndProductModel(SpecField field, ProductModel productModel);
    boolean existsByFieldAndSku(SpecField field, SKU sku);
    boolean existsByFieldIdAndProductModelId(Long fieldId, Long productModelId);
    boolean existsByFieldIdAndSkuId(Long fieldId, Long skuId);

    // Xóa theo điều kiện
    void deleteByProductModel(ProductModel productModel);
    void deleteByProductModelId(Long productModelId);
    void deleteBySkuId(Long skuId);
    void deleteByFieldId(Long fieldId);

    // Thống kê
    @Query("SELECT COUNT(sv) FROM SpecValue sv WHERE sv.productModel.id = :productModelId")
    long countByProductModelId(@Param("productModelId") Long productModelId);

    @Query("SELECT COUNT(sv) FROM SpecValue sv WHERE sv.sku.id = :skuId")
    long countBySkuId(@Param("skuId") Long skuId);

    @Query("SELECT COUNT(sv) FROM SpecValue sv WHERE sv.field.id = :fieldId")
    long countByFieldId(@Param("fieldId") Long fieldId);

    // Tìm kiếm theo giá trị
    @Query("SELECT sv FROM SpecValue sv WHERE " +
           "sv.field.dataType = 'TEXT' AND " +
           "LOWER(sv.textValue) LIKE LOWER(CONCAT('%', :value, '%'))")
    List<SpecValue> findByTextValueContaining(@Param("value") String value);

    @Query("SELECT sv FROM SpecValue sv WHERE " +
           "sv.field.dataType = 'NUMBER' AND " +
           "sv.numberValue = :value")
    List<SpecValue> findByNumberValue(@Param("value") Long value);

    @Query("SELECT sv FROM SpecValue sv WHERE " +
           "sv.field.dataType = 'DECIMAL' AND " +
           "sv.decimalValue = :value")
    List<SpecValue> findByDecimalValue(@Param("value") Double value);

    @Query("SELECT sv FROM SpecValue sv WHERE " +
           "sv.field.dataType = 'BOOLEAN' AND " +
           "sv.booleanValue = :value")
    List<SpecValue> findByBooleanValue(@Param("value") Boolean value);

    // Tìm kiếm tất cả spec values cho một sản phẩm với group info
    @Query("SELECT sv FROM SpecValue sv " +
           "JOIN FETCH sv.field f " +
           "JOIN FETCH f.group g " +
           "WHERE sv.productModel.id = :productModelId " +
           "ORDER BY g.sortOrder, f.sortOrder")
    List<SpecValue> findAllByProductModelIdWithGroupAndField(@Param("productModelId") Long productModelId);

    @Query("SELECT sv FROM SpecValue sv " +
           "JOIN FETCH sv.field f " +
           "JOIN FETCH f.group g " +
           "WHERE sv.sku.id = :skuId " +
           "ORDER BY g.sortOrder, f.sortOrder")
    List<SpecValue> findAllBySkuIdWithGroupAndField(@Param("skuId") Long skuId);
}
