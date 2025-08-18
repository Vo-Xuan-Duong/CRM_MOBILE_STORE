package com.example.Backend.services;

import com.example.Backend.dtos.product.SKURequest;
import com.example.Backend.dtos.product.SKUResponse;
import com.example.Backend.exceptions.ProductException;
import com.example.Backend.mappers.SKUMapper;
import com.example.Backend.models.ProductModel;
import com.example.Backend.models.SKU;
import com.example.Backend.repositorys.ProductModelRepository;
import com.example.Backend.repositorys.SKURepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SKUService {

    private final SKURepository skuRepository;
    private final ProductModelRepository productModelRepository;
    private final SKUMapper skuMapper;

    public SKUResponse createSKU(SKURequest request) {
        ProductModel model = findProductModelById(request.getModelId());

        // Check if SKU already exists
        if (skuRepository.findByModelIdAndVariant(
                request.getModelId(),
                request.getVariantName(),
                request.getColor(),
                request.getStorageGb()).isPresent()) {
            throw new ProductException("SKU already exists with these specifications");
        }

        // Check barcode uniqueness
        if (request.getBarcode() != null && skuRepository.existsByBarcode(request.getBarcode())) {
            throw new ProductException("Barcode already exists: " + request.getBarcode());
        }

        SKU sku = SKU.builder()
                .model(model)
                .variantName(request.getVariantName())
                .color(request.getColor())
                .storageGb(request.getStorageGb())
                .barcode(request.getBarcode())
                .price(request.getPrice())
                .costPrice(request.getCostPrice())
                .isSerialized(request.getIsSerialized())
                .isActive(request.getIsActive())
                .build();

        SKU savedSKU = skuRepository.save(sku);
        return skuMapper.toResponse(savedSKU);
    }

    public SKUResponse updateSKU(Long id, SKURequest request) {
        SKU sku = findSKUById(id);
        ProductModel model = findProductModelById(request.getModelId());

        sku.setModel(model);
        sku.setVariantName(request.getVariantName());
        sku.setColor(request.getColor());
        sku.setStorageGb(request.getStorageGb());
        sku.setBarcode(request.getBarcode());
        sku.setPrice(request.getPrice());
        sku.setCostPrice(request.getCostPrice());
        sku.setIsSerialized(request.getIsSerialized());
        sku.setIsActive(request.getIsActive());

        SKU savedSKU = skuRepository.save(sku);
        return skuMapper.toResponse(savedSKU);
    }

    public SKUResponse getSKUById(Long id) {
        SKU sku = findSKUById(id);
        return skuMapper.toResponse(sku);
    }

    public SKUResponse getSKUByBarcode(String barcode) {
        SKU sku = skuRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ProductException("SKU not found with barcode: " + barcode));
        return skuMapper.toResponse(sku);
    }

    public Page<SKUResponse> getAllSKUs(Pageable pageable) {
        return skuRepository.findByIsActiveTrue(pageable)
                .map(skuMapper::toResponse);
    }

    public Page<SKUResponse> searchSKUs(String keyword, Pageable pageable) {
        return skuRepository.findActiveSkusWithSearch(keyword, pageable)
                .map(skuMapper::toResponse);
    }

    public List<SKUResponse> getSKUsByProductModel(Long modelId) {
        return skuRepository.findByModelIdAndIsActiveTrue(modelId).stream()
                .map(skuMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<SKUResponse> getSKUsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return skuRepository.findByPriceRange(minPrice, maxPrice).stream()
                .map(skuMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<SKUResponse> getSKUsByBrand(Long brandId) {
        return skuRepository.findByBrandId(brandId).stream()
                .map(skuMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void deactivateSKU(Long id) {
        SKU sku = findSKUById(id);
        sku.setIsActive(false);
        skuRepository.save(sku);
    }

    public void activateSKU(Long id) {
        SKU sku = findSKUById(id);
        sku.setIsActive(true);
        skuRepository.save(sku);
    }

    public void updateSKUPrice(Long id, BigDecimal newPrice) {
        SKU sku = findSKUById(id);
        sku.setPrice(newPrice);
        skuRepository.save(sku);
    }

    private SKU findSKUById(Long id) {
        return skuRepository.findById(id)
                .orElseThrow(() -> new ProductException("SKU not found with id: " + id));
    }

    private ProductModel findProductModelById(Long id) {
        return productModelRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product model not found with id: " + id));
    }
}
