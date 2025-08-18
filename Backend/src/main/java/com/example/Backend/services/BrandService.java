package com.example.Backend.services;

import com.example.Backend.models.Brand;
import com.example.Backend.repositorys.BrandRepository;
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
    public Brand createBrand(Brand brand) {
        log.info("Tạo thương hiệu mới: {}", brand.getName());

        // Validate dữ liệu đầu vào
        validateBrandData(brand);

        // Kiểm tra tên thương hiệu đã tồn tại
        if (brandRepository.existsByName(brand.getName().trim())) {
            throw new RuntimeException("Tên thương hiệu '" + brand.getName() + "' đã tồn tại");
        }

        // Chuẩn hóa dữ liệu
        brand.setName(brand.getName().trim());
        if (brand.getCountry() != null) {
            brand.setCountry(brand.getCountry().trim());
        }
        if (brand.getWebsite() != null) {
            brand.setWebsite(brand.getWebsite().trim());
        }

        Brand savedBrand = brandRepository.save(brand);
        log.info("Đã tạo thương hiệu thành công với ID: {}", savedBrand.getId());
        return savedBrand;
    }

    // Cập nhật thương hiệu
    public Brand updateBrand(Long id, Brand brandDetails) {
        log.info("Cập nhật thương hiệu ID: {}", id);

        Brand existingBrand = getBrandById(id);

        // Validate dữ liệu đầu vào
        validateBrandData(brandDetails);

        // Kiểm tra tên thương hiệu (nếu thay đổi)
        String newName = brandDetails.getName().trim();
        if (!existingBrand.getName().equals(newName) && brandRepository.existsByName(newName)) {
            throw new RuntimeException("Tên thương hiệu '" + newName + "' đã tồn tại");
        }

        // Cập nhật thông tin
        existingBrand.setName(newName);
        if (brandDetails.getCountry() != null) {
            existingBrand.setCountry(brandDetails.getCountry().trim());
        }
        if (brandDetails.getWebsite() != null) {
            existingBrand.setWebsite(brandDetails.getWebsite().trim());
        }

        Brand savedBrand = brandRepository.save(existingBrand);
        log.info("Đã cập nhật thương hiệu thành công");
        return savedBrand;
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
    public Page<Brand> getAllBrands(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return brandRepository.findAll(pageable);
    }

    // Tìm kiếm thương hiệu
    @Transactional(readOnly = true)
    public Page<Brand> searchBrands(String keyword, int page, int size) {
        if (!StringUtils.hasText(keyword)) {
            return getAllBrands(page, size, "name", "asc");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return brandRepository.findByNameOrCodeContainingIgnoreCase(keyword.trim(), pageable);
    }

    // Lấy thương hiệu có sản phẩm
    @Transactional(readOnly = true)
    public List<Brand> getBrandsWithProducts() {
        return brandRepository.findBrandsWithProducts();
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

    // Lấy thương hiệu theo quốc gia
    @Transactional(readOnly = true)
    public List<Brand> getBrandsByCountry(String country) {
        if (!StringUtils.hasText(country)) {
            throw new RuntimeException("Tên quốc gia không được để trống");
        }
        return brandRepository.findByCountryIgnoreCase(country.trim());
    }

    // Private method để validate dữ liệu
    private void validateBrandData(Brand brand) {
        if (brand == null) {
            throw new RuntimeException("Dữ liệu thương hiệu không được null");
        }

        if (!StringUtils.hasText(brand.getName())) {
            throw new RuntimeException("Tên thương hiệu không được để trống");
        }

        if (brand.getName().trim().length() > 255) {
            throw new RuntimeException("Tên thương hiệu không được vượt quá 255 ký tự");
        }

        if (brand.getWebsite() != null && !brand.getWebsite().trim().isEmpty()) {
            String website = brand.getWebsite().trim();
            if (!website.startsWith("http://") && !website.startsWith("https://")) {
                throw new RuntimeException("Website phải bắt đầu bằng http:// hoặc https://");
            }
        }
    }
}
