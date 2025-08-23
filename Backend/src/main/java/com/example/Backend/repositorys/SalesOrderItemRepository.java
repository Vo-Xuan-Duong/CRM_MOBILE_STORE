package com.example.Backend.repositorys;

import com.example.Backend.models.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {

    List<SalesOrderItem> findByOrder_Id(Long orderId);

    List<SalesOrderItem> findBySku_Id(Long skuId);

    @Query("SELECT SUM(soi.quantity) FROM SalesOrderItem soi WHERE soi.sku.id = :skuId")
    Long getTotalQuantitySoldForSku(@Param("skuId") Long skuId);

    @Query("SELECT soi FROM SalesOrderItem soi WHERE soi.order.id = :orderId AND soi.sku.id = :skuId")
    SalesOrderItem findByOrderAndSku(@Param("orderId") Long orderId, @Param("skuId") Long skuId);
}
