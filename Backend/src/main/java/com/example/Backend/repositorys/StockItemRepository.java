package com.example.Backend.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.models.SKU;
import com.example.Backend.models.StockItem;

@Repository
public interface StockItemRepository extends JpaRepository<StockItem, Long> {

    Optional<StockItem> findBySkuId(Long skuId);

    Optional<StockItem> findBySku(SKU sku);

    @Query("SELECT si FROM StockItem si WHERE si.quantity <= si.minStock")
    List<StockItem> findLowStockItems();

    @Query("SELECT si FROM StockItem si WHERE si.quantity = 0")
    List<StockItem> findOutOfStockItems();

    @Query("SELECT si FROM StockItem si WHERE si.quantity > 0")
    List<StockItem> findInStockItems();

    @Query("SELECT si FROM StockItem si WHERE si.reservedQty > 0")
    List<StockItem> findItemsWithReservations();

    @Query("SELECT si FROM StockItem si WHERE (si.quantity - si.reservedQty) >= :requiredQty")
    List<StockItem> findItemsWithAvailableQuantity(@Param("requiredQty") Integer requiredQty);

    @Modifying
    @Query("UPDATE StockItem si SET si.quantity = si.quantity + :quantity WHERE si.sku.id = :skuId")
    void increaseStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE StockItem si SET si.quantity = si.quantity - :quantity WHERE si.sku.id = :skuId AND si.quantity >= :quantity")
    int decreaseStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE StockItem si SET si.reservedQty = si.reservedQty + :quantity WHERE si.sku.id = :skuId AND (si.quantity - si.reservedQty) >= :quantity")
    int reserveStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE StockItem si SET si.reservedQty = si.reservedQty - :quantity WHERE si.sku.id = :skuId AND si.reservedQty >= :quantity")
    int releaseReservation(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    @Query("SELECT SUM(si.quantity) FROM StockItem si")
    Long getTotalStockQuantity();

    @Query("SELECT SUM(si.reservedQty) FROM StockItem si")
    Long getTotalReservedQuantity();

    @Query("SELECT COUNT(si) FROM StockItem si WHERE si.quantity <= si.minStock")
    long countLowStockItems();
}
