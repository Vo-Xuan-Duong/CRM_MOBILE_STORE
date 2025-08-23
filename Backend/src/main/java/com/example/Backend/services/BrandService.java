package com.example.Backend.services;

import com.example.Backend.dtos.brand.BrandCreateDTO;
import com.example.Backend.dtos.brand.BrandResponseDTO;
import com.example.Backend.dtos.brand.BrandUpdateDTO;
import com.example.Backend.models.Brand;
import com.example.Backend.repositorys.BrandRepository;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BrandService {

    private final BrandRepository brandRepository;

    // Tạo thương hiệu mới
    public BrandResponseDTO createBrand(BrandCreateDTO brandCreateDTO) {
        log.info("Tạo thương hiệu mới: {}", brandCreateDTO.getName());

        // Kiểm tra tên thương hiệu đã tồn tại
        if (brandRepository.existsByName(brandCreateDTO.getName().trim())) {
            throw new RuntimeException("Tên thương hiệu '" + brandCreateDTO.getName() + "' đã tồn tại");
        }

        Brand brand = Brand.builder()
                .name(brandCreateDTO.getName())
                .logoUrl(brandCreateDTO.getLogoUrl())
                .website(brandCreateDTO.getWebsite())
                .isActive(true) // Mặc định là hoạt động
                .build();

        Brand savedBrand = brandRepository.save(brand);
        log.info("Đã tạo thương hiệu thành công với ID: {}", savedBrand.getId());
        return toBrandResponseDTO(savedBrand);
    }

    // Cập nhật thương hiệu
    public BrandResponseDTO updateBrand(Long id, BrandUpdateDTO brandUpdateDTO) {
        log.info("Cập nhật thương hiệu ID: {}", id);

        Brand existingBrand = getBrandById(id);

        // Kiểm tra tên thương hiệu (nếu thay đổi)
        String newName = brandUpdateDTO.getName().trim();
        if (!existingBrand.getName().equals(newName) && brandRepository.existsByName(newName)) {
            throw new RuntimeException("Tên thương hiệu '" + newName + "' đã tồn tại");
        }

        // Cập nhật thông tin
        existingBrand.setName(newName);
        if (brandUpdateDTO.getLogoUrl() != null) {
            existingBrand.setLogoUrl(brandUpdateDTO.getLogoUrl().trim());
        }
        if (brandUpdateDTO.getWebsite() != null) {
            existingBrand.setWebsite(brandUpdateDTO.getWebsite().trim());
        }

        Brand savedBrand = brandRepository.save(existingBrand);
        log.info("Đã cập nhật thương hiệu thành công");
        return toBrandResponseDTO(savedBrand);
    }

    // Lấy thương hiệu theo ID
    @Transactional(readOnly = true)
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu với ID: " + id));
    }

    // Lấy tất cả thương hiệu
    @Transactional(readOnly = true)
    public List<Brand> getAllBrands() {
        return brandRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    // Lấy thương hiệu có phân trang
    @Transactional(readOnly = true)
    public Page<Brand> getAllBrands(Pageable pageable) {
        return brandRepository.findAll(pageable);
    }

    // Lấy thương hiệu có sản phẩm
    @Transactional(readOnly = true)
    public List<BrandResponseDTO> getBrandsWithProducts() {
        List<Brand> brands = brandRepository.findBrandsWithProducts();
        return brands.stream()
                .map(this::toBrandResponseDTO)
                .toList();
    }

    // Lấy thương hiệu phổ biến nhất
    @Transactional(readOnly = true)
    public List<Brand> getTopBrands(int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(1, limit));
        return brandRepository.findTopBrandsByPopularity(pageable);
    }

    // Xóa thương hiệu
    public void deleteBrand(Long id) {
        log.info("Xóa thương hiệu ID: {}", id);

        Brand brand = getBrandById(id);

        // Kiểm tra xem thương hiệu có đang được sử dụng không
        List<Brand> brandsWithProducts = brandRepository.findBrandsWithProducts();
        boolean hasProducts = brandsWithProducts.stream()
                .anyMatch(b -> b.getId().equals(id));

        if (hasProducts) {
            throw new RuntimeException("Không thể xóa thương hiệu vì đang có sản phẩm liên kết");
        }

        brandRepository.delete(brand);
        log.info("Đã xóa thương hiệu thành công");
    }

    // Thống kê thương hiệu
    @Transactional(readOnly = true)
    public long getTotalBrands() {
        return brandRepository.count();
    }

    @Transactional(readOnly = true)
    public long getBrandsWithProductsCount() {
        return brandRepository.countBrandsWithProducts();
    }

    // Tìm thương hiệu theo tên
    @Transactional(readOnly = true)
    public Brand findByName(String name) {
        return brandRepository.findByName(name.trim())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu với tên: " + name));
    }


    public BrandResponseDTO toBrandResponseDTO(Brand brand) {
        if (brand == null) {
            return null;
        }



        return BrandResponseDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .logoUrl(brand.getLogoUrl())
                .website(brand.getWebsite())
                .isActive(brand.getIsActive())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                // Thêm thông tin thống kê nếu cần
//                .productCount(brand.getProducts() != null ? (long) brand.getProducts().size() : 0)
//                .modelCount(brand.getModels() != null ? (long) brand.getModels().size() : 0)
                .build();
    }

    public void activateBrand(@Min(1) Long id) {
        log.info("Kích hoạt thương hiệu ID: {}", id);
        Brand brand = getBrandById(id);
        brand.setIsActive(true);
        brandRepository.save(brand);
        log.info("Đã kích hoạt thương hiệu thành công");
    }

    public void deactivateBrand(@Min(1) Long id) {
        log.info("Vô hiệu hóa thương hiệu ID: {}", id);
        Brand brand = getBrandById(id);
        brand.setIsActive(false);
        brandRepository.save(brand);
        log.info("Đã vô hiệu hóa thương hiệu thành công");
    }

    public List<Brand> getActiveBrands() {
        log.info("Lấy danh sách thương hiệu đang hoạt động");
        return brandRepository.findByIsActiveTrue(Sort.by(Sort.Direction.ASC, "name"));
    }

    public List<Brand> getInactiveBrands() {
        log.info("Lấy danh sách thương hiệu không hoạt động");
        return brandRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .filter(brand -> !brand.getIsActive())
                .toList();
    }
}
