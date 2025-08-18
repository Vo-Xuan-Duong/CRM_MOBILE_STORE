package com.example.Backend.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.models.StockMovement;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findBySkuIdOrderByCreatedAtDesc(Long skuId);

    Page<StockMovement> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<StockMovement> findByMovementType(StockMovement.MovementType movementType);

    List<StockMovement> findByReason(StockMovement.MovementReason reason);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.refType = :refType AND sm.refId = :refId")
    List<StockMovement> findByReference(@Param("refType") String refType, @Param("refId") Long refId);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdBy.id = :userId ORDER BY sm.createdAt DESC")
    List<StockMovement> findByCreatedById(@Param("userId") Long userId);
}
