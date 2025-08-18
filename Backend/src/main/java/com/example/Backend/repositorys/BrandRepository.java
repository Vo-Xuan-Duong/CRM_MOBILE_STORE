package com.example.Backend.repositorys;

import com.example.Backend.models.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    // Tìm theo tên thương hiệu
    Optional<Brand> findByName(String name);

    // Tìm kiếm theo tên (không phân biệt hoa thường)
    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Brand> findByNameContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);

    // Tìm kiếm theo tên hoặc mã (cho method searchBrands)
    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.country) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Brand> findByNameOrCodeContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);

    // Tìm theo quốc gia
    List<Brand> findByCountryIgnoreCase(String country);

    // Kiểm tra tên thương hiệu đã tồn tại
    boolean existsByName(String name);

    // Lấy thương hiệu có sản phẩm
    @Query("SELECT DISTINCT b FROM Brand b INNER JOIN Model m ON b.id = m.brand.id")
    List<Brand> findBrandsWithProducts();

    // Lấy thương hiệu phổ biến nhất (theo số lượng model)
    @Query("SELECT b FROM Brand b LEFT JOIN Model m ON b.id = m.brand.id GROUP BY b.id ORDER BY COUNT(m.id) DESC")
    List<Brand> findTopBrandsByPopularity(Pageable pageable);

    // Đếm số thương hiệu có sản phẩm
    @Query("SELECT COUNT(DISTINCT b.id) FROM Brand b INNER JOIN Model m ON b.id = m.brand.id")
    long countBrandsWithProducts();
}
