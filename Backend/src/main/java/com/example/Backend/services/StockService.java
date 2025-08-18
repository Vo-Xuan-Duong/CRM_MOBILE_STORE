package com.example.Backend.services;

import com.example.Backend.dtos.stock.StockItemResponse;
import com.example.Backend.dtos.stock.StockMovementRequest;
import com.example.Backend.dtos.stock.StockMovementResponse;
import com.example.Backend.exceptions.StockException;
import com.example.Backend.mappers.StockItemMapper;
import com.example.Backend.mappers.StockMovementMapper;
import com.example.Backend.models.*;
import com.example.Backend.repositorys.*;
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
public class StockService {

    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final SKURepository skuRepository;
    private final UserRepository userRepository;
    private final SerialUnitRepository serialUnitRepository;
    private final StockItemMapper stockItemMapper;
    private final StockMovementMapper stockMovementMapper;

    public StockItemResponse getStockBySkuId(Long skuId) {
        StockItem stockItem = stockItemRepository.findBySkuId(skuId)
                .orElseThrow(() -> new StockException("Stock item not found for SKU: " + skuId));
        return stockItemMapper.toResponse(stockItem);
    }

    public List<StockItemResponse> getAllStockItems() {
        return stockItemRepository.findAll().stream()
                .map(stockItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<StockItemResponse> getLowStockItems() {
        return stockItemRepository.findLowStockItems().stream()
                .map(stockItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<StockItemResponse> getOutOfStockItems() {
        return stockItemRepository.findOutOfStockItems().stream()
                .map(stockItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<StockItemResponse> getInStockItems() {
        return stockItemRepository.findInStockItems().stream()
                .map(stockItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    public StockMovementResponse addStock(StockMovementRequest request, Long userId) {
        SKU sku = findSkuById(request.getSkuId());
        User user = findUserById(userId);

        // Update or create stock item
        StockItem stockItem = stockItemRepository.findBySkuId(request.getSkuId())
                .orElse(StockItem.builder()
                        .sku(sku)
                        .quantity(0)
                        .reservedQty(0)
                        .minStock(request.getMinStock() != null ? request.getMinStock() : 0)
                        .maxStock(request.getMaxStock())
                        .build());

        stockItem.setQuantity(stockItem.getQuantity() + request.getQuantity());
        if (request.getMinStock() != null) {
            stockItem.setMinStock(request.getMinStock());
        }
        if (request.getMaxStock() != null) {
            stockItem.setMaxStock(request.getMaxStock());
        }

        stockItemRepository.save(stockItem);

        // Create stock movement record
        StockMovement movement = StockMovement.builder()
                .sku(sku)
                .movementType(StockMovement.MovementType.IN)
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .refType(request.getRefType())
                .refId(request.getRefId())
                .notes(request.getNotes())
                .createdBy(user)
                .build();

        StockMovement savedMovement = stockMovementRepository.save(movement);
        return stockMovementMapper.toResponse(savedMovement);
    }

    public StockMovementResponse removeStock(StockMovementRequest request, Long userId) {
        SKU sku = findSkuById(request.getSkuId());
        User user = findUserById(userId);

        StockItem stockItem = stockItemRepository.findBySkuId(request.getSkuId())
                .orElseThrow(() -> new StockException("Stock item not found for SKU: " + request.getSkuId()));

        if (stockItem.getAvailableQuantity() < request.getQuantity()) {
            throw new StockException("Insufficient available stock. Available: " +
                    stockItem.getAvailableQuantity() + ", Requested: " + request.getQuantity());
        }

        stockItem.setQuantity(stockItem.getQuantity() - request.getQuantity());
        stockItemRepository.save(stockItem);

        // Create stock movement record
        StockMovement movement = StockMovement.builder()
                .sku(sku)
                .movementType(StockMovement.MovementType.OUT)
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .refType(request.getRefType())
                .refId(request.getRefId())
                .notes(request.getNotes())
                .createdBy(user)
                .build();

        StockMovement savedMovement = stockMovementRepository.save(movement);
        return stockMovementMapper.toResponse(savedMovement);
    }

    public StockItemResponse adjustStock(Long skuId, Integer newQuantity, String reason, Long userId) {
        StockItem stockItem = stockItemRepository.findBySkuId(skuId)
                .orElseThrow(() -> new StockException("Stock item not found for SKU: " + skuId));

        int difference = newQuantity - stockItem.getQuantity();
        if (difference == 0) {
            return stockItemMapper.toResponse(stockItem);
        }

        // Create adjustment movement
        StockMovementRequest request = StockMovementRequest.builder()
                .skuId(skuId)
                .quantity(Math.abs(difference))
                .reason(StockMovement.MovementReason.ADJUSTMENT)
                .notes(reason)
                .build();

        if (difference > 0) {
            return stockItemMapper.toResponse(
                    stockItemRepository.findBySkuId(skuId).get()
            );
        } else {
            return stockItemMapper.toResponse(
                    stockItemRepository.findBySkuId(skuId).get()
            );
        }
    }

    public boolean reserveStock(Long skuId, Integer quantity) {
        int reserved = stockItemRepository.reserveStock(skuId, quantity);
        return reserved > 0;
    }

    public boolean releaseReservation(Long skuId, Integer quantity) {
        int released = stockItemRepository.releaseReservation(skuId, quantity);
        return released > 0;
    }

    public Page<StockMovementResponse> getStockMovements(Pageable pageable) {
        return stockMovementRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(stockMovementMapper::toResponse);
    }

    public List<StockMovementResponse> getStockMovementsBySkuId(Long skuId) {
        return stockMovementRepository.findBySkuIdOrderByCreatedAtDesc(skuId).stream()
                .map(stockMovementMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Long getTotalStockValue() {
        return stockItemRepository.findAll().stream()
                .mapToLong(stockItem -> {
                    if (stockItem.getSku().getCostPrice() != null) {
                        return stockItem.getQuantity() * stockItem.getSku().getCostPrice().longValue();
                    }
                    return 0L;
                })
                .sum();
    }

    public Long getTotalStockQuantity() {
        Long total = stockItemRepository.getTotalStockQuantity();
        return total != null ? total : 0L;
    }

    public Long getTotalReservedQuantity() {
        Long total = stockItemRepository.getTotalReservedQuantity();
        return total != null ? total : 0L;
    }

    public long getLowStockItemCount() {
        return stockItemRepository.countLowStockItems();
    }

    public void updateStockLevels(Long skuId, Integer minStock, Integer maxStock) {
        StockItem stockItem = stockItemRepository.findBySkuId(skuId)
                .orElseThrow(() -> new StockException("Stock item not found for SKU: " + skuId));

        stockItem.setMinStock(minStock);
        stockItem.setMaxStock(maxStock);
        stockItemRepository.save(stockItem);
    }

    private SKU findSkuById(Long id) {
        return skuRepository.findById(id)
                .orElseThrow(() -> new StockException("SKU not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new StockException("User not found with id: " + id));
    }
}
