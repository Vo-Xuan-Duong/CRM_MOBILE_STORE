package com.example.Backend.services;

import com.example.Backend.dtos.product.ProductModelRequest;
import com.example.Backend.dtos.product.ProductModelResponse;
import com.example.Backend.exceptions.ProductException;
import com.example.Backend.mappers.ProductModelMapper;
import com.example.Backend.models.Brand;
import com.example.Backend.models.ProductModel;
import com.example.Backend.repositorys.BrandRepository;
import com.example.Backend.repositorys.ProductModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductModelService {

    private final ProductModelRepository productModelRepository;
    private final BrandRepository brandRepository;
    private final ProductModelMapper productModelMapper;

    public ProductModelResponse createProductModel(ProductModelRequest request) {
        Brand brand = findBrandById(request.getBrandId());

        // Check if product model already exists
        if (productModelRepository.existsByBrandAndName(brand, request.getName())) {
            throw new ProductException("Product model already exists: " + request.getName());
        }

        ProductModel productModel = ProductModel.builder()
                .brand(brand)
                .name(request.getName())
                .category(ProductModel.ProductCategory.valueOf(request.getCategory().toUpperCase()))
                .defaultWarrantyMonths(request.getDefaultWarrantyMonths())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .build();

        ProductModel savedModel = productModelRepository.save(productModel);
        return productModelMapper.toResponse(savedModel);
    }

    public ProductModelResponse updateProductModel(Long id, ProductModelRequest request) {
        ProductModel productModel = findProductModelById(id);
        Brand brand = findBrandById(request.getBrandId());

        productModel.setBrand(brand);
        productModel.setName(request.getName());
        productModel.setCategory(ProductModel.ProductCategory.valueOf(request.getCategory().toUpperCase()));
        productModel.setDefaultWarrantyMonths(request.getDefaultWarrantyMonths());
        productModel.setDescription(request.getDescription());
        productModel.setIsActive(request.getIsActive());

        ProductModel savedModel = productModelRepository.save(productModel);
        return productModelMapper.toResponse(savedModel);
    }

    public ProductModelResponse getProductModelById(Long id) {
        ProductModel productModel = findProductModelById(id);
        return productModelMapper.toResponse(productModel);
    }

    public Page<ProductModelResponse> getAllProductModels(Pageable pageable) {
        return productModelRepository.findByIsActiveTrue(pageable)
                .map(productModelMapper::toResponse);
    }

    public Page<ProductModelResponse> searchProductModels(String keyword, Pageable pageable) {
        return productModelRepository.findActiveModelsWithSearch(keyword, pageable)
                .map(productModelMapper::toResponse);
    }

    public List<ProductModelResponse> getProductModelsByBrand(Long brandId) {
        return productModelRepository.findByBrandIdAndIsActiveTrue(brandId).stream()
                .map(productModelMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductModelResponse> getProductModelsByCategory(String category) {
        ProductModel.ProductCategory productCategory = ProductModel.ProductCategory.valueOf(category.toUpperCase());
        return productModelRepository.findByCategory(productCategory).stream()
                .filter(ProductModel::getIsActive)
                .map(productModelMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void deactivateProductModel(Long id) {
        ProductModel productModel = findProductModelById(id);
        productModel.setIsActive(false);
        productModelRepository.save(productModel);
    }

    public void activateProductModel(Long id) {
        ProductModel productModel = findProductModelById(id);
        productModel.setIsActive(true);
        productModelRepository.save(productModel);
    }

    private ProductModel findProductModelById(Long id) {
        return productModelRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product model not found with id: " + id));
    }

    private Brand findBrandById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ProductException("Brand not found with id: " + id));
    }
}
