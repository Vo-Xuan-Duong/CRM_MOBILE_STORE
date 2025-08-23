package com.example.Backend.dtos.product;

import com.example.Backend.models.ProductModel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductModelRequest {

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotBlank(message = "Product model name is required")
    private String name;

    private ProductModel.ProductCategory category = ProductModel.ProductCategory.PHONE; // phone, accessory, service

    @Min(value = 0, message = "Warranty months must be non-negative")
    private Integer defaultWarrantyMonths = 12;

    private String description;

}
