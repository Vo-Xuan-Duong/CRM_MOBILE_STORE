package com.example.Backend.repositorys;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.models.ProductModel;
import com.example.Backend.models.SKU;

@Repository
public interface SKURepository extends JpaRepository<SKU, Long> {

    List<SKU> findByModel(ProductModel model);

    List<SKU> findByModelId(Long modelId);

    Optional<SKU> findByBarcode(String barcode);

    List<SKU> findByIsActiveTrue();

    Page<SKU> findByIsActiveTrue(Pageable pageable);

    List<SKU> findByModelIdAndIsActiveTrue(Long modelId);

    @Query("SELECT s FROM SKU s WHERE s.model.id = :modelId AND " +
           "(:variantName IS NULL OR s.variantName = :variantName) AND " +
           "(:color IS NULL OR s.color = :color) AND " +
           "(:storageGb IS NULL OR s.storageGb = :storageGb)")
    Optional<SKU> findByModelIdAndVariant(@Param("modelId") Long modelId,
                                         @Param("variantName") String variantName,
                                         @Param("color") String color,
                                         @Param("storageGb") Integer storageGb);

    @Query("SELECT s FROM SKU s WHERE s.isActive = true AND " +
           "(LOWER(s.model.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.model.brand.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.variantName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.color) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "s.barcode LIKE CONCAT('%', :search, '%'))")
    Page<SKU> findActiveSkusWithSearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT s FROM SKU s WHERE s.price BETWEEN :minPrice AND :maxPrice AND s.isActive = true")
    List<SKU> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT s FROM SKU s WHERE s.model.brand.id = :brandId AND s.isActive = true")
    List<SKU> findByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT s.color, COUNT(s) FROM SKU s WHERE s.isActive = true AND s.color IS NOT NULL GROUP BY s.color")
    List<Object[]> getSkuCountByColor();

    @Query("SELECT s.storageGb, COUNT(s) FROM SKU s WHERE s.isActive = true AND s.storageGb IS NOT NULL GROUP BY s.storageGb ORDER BY s.storageGb")
    List<Object[]> getSkuCountByStorage();

    boolean existsByBarcode(String barcode);

    @Query("SELECT COUNT(s) FROM SKU s WHERE s.model.id = :modelId AND s.isActive = true")
    long countByModelIdAndIsActiveTrue(@Param("modelId") Long modelId);

    @Query("SELECT s FROM SKU s WHERE s.isSerialized = true AND s.isActive = true")
    List<SKU> findSerializedSkus();

    @Query("SELECT s FROM SKU s WHERE s.isSerialized = false AND s.isActive = true")
    List<SKU> findNonSerializedSkus();
}
