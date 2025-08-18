package com.example.Backend.mappers;

import com.example.Backend.dtos.product.ProductModelResponse;
import com.example.Backend.models.ProductModel;
import org.springframework.stereotype.Component;

@Component
public class ProductModelMapper {

    public ProductModelResponse toResponse(ProductModel productModel) {
        if (productModel == null) {
            return null;
        }

        return ProductModelResponse.builder()
                .id(productModel.getId())
                .brandId(productModel.getBrand().getId())
                .brandName(productModel.getBrand().getName())
                .name(productModel.getName())
                .category(productModel.getCategory().getValue())
                .defaultWarrantyMonths(productModel.getDefaultWarrantyMonths())
                .description(productModel.getDescription())
                .isActive(productModel.getIsActive())
                .createdAt(productModel.getCreatedAt())
                .updatedAt(productModel.getUpdatedAt())
                .build();
    }
}
