package com.example.Backend.mappers;

import com.example.Backend.dtos.product.ProductCreateRequestDTO;
import com.example.Backend.dtos.product.ProductResponseDTO;
import com.example.Backend.dtos.product.ProductUpdateRequestDTO;
import com.example.Backend.models.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .unitPrice(product.getUnitPrice())
                .attributes(product.getAttributes())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public Product toEntity(ProductCreateRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Product.builder()
                .name(dto.getName())
                .sku(dto.getSku())
                .unitPrice(dto.getUnitPrice())
                .attributes(dto.getAttributes())
                .isActive(Boolean.TRUE)
                .build();
    }

    public void updateEntity(Product product, ProductUpdateRequestDTO dto) {
        if (product == null || dto == null) {
            return;
        }

        // Chỉ update các field không null
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());

        // Thông tin giá cả
        if (dto.getUnitPrice() != null) product.setUnitPrice(dto.getUnitPrice());

        // Thông tin kho hàng
        

        // Thông tin sản phẩm cơ bản
        

        // Thông số hiệu suất
        

        // Thông số màn hình
        

        // Camera
        

        // Pin và sạc
        

        // Kết nối
        

        // Cảm biến và bảo mật
        

        // Audio
        

        // Thiết kế và vật liệu
        

        // SIM
        

        // Tính năng đặc biệt
        

        // Bảo hành
        

        // Hình ảnh và media
        

        // Thông tin kinh doanh
        

        // SEO và metadata
        
    }

    public List<ProductResponseDTO> toResponseDTOList(List<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
