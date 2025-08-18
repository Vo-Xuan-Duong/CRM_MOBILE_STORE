package com.example.Backend.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.models.Brand;
import com.example.Backend.models.ProductModel;
import com.example.Backend.models.ProductModel.ProductCategory;

@Repository
public interface ProductModelRepository extends JpaRepository<ProductModel, Long> {

    List<ProductModel> findByBrand(Brand brand);

    List<ProductModel> findByBrandId(Long brandId);

    List<ProductModel> findByCategory(ProductCategory category);

    List<ProductModel> findByIsActiveTrue();

    Page<ProductModel> findByIsActiveTrue(Pageable pageable);

    Optional<ProductModel> findByBrandAndName(Brand brand, String name);

    @Query("SELECT pm FROM ProductModel pm WHERE pm.brand.id = :brandId AND pm.name = :name")
    Optional<ProductModel> findByBrandIdAndName(@Param("brandId") Long brandId, @Param("name") String name);

    @Query("SELECT pm FROM ProductModel pm WHERE pm.isActive = true AND " +
           "(LOWER(pm.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(pm.brand.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ProductModel> findActiveModelsWithSearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT pm FROM ProductModel pm WHERE pm.brand.id = :brandId AND pm.category = :category AND pm.isActive = true")
    List<ProductModel> findByBrandIdAndCategoryAndIsActiveTrue(@Param("brandId") Long brandId,
                                                              @Param("category") ProductCategory category);

    @Query("SELECT pm.category, COUNT(pm) FROM ProductModel pm WHERE pm.isActive = true GROUP BY pm.category")
    List<Object[]> getModelCountByCategory();

    @Query("SELECT b.name, COUNT(pm) FROM ProductModel pm JOIN pm.brand b WHERE pm.isActive = true GROUP BY b.name ORDER BY COUNT(pm) DESC")
    List<Object[]> getModelCountByBrand();

    boolean existsByBrandAndName(Brand brand, String name);

    @Query("SELECT COUNT(pm) FROM ProductModel pm WHERE pm.brand.id = :brandId AND pm.isActive = true")
    long countByBrandIdAndIsActiveTrue(@Param("brandId") Long brandId);
}
