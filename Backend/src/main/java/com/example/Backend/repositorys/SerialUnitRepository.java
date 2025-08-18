package com.example.Backend.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.models.SerialUnit;
import com.example.Backend.models.SerialUnit.SerialStatus;

@Repository
public interface SerialUnitRepository extends JpaRepository<SerialUnit, Long> {

    Optional<SerialUnit> findByImei(String imei);

    List<SerialUnit> findBySkuId(Long skuId);

    List<SerialUnit> findByStatus(SerialStatus status);

    @Query("SELECT su FROM SerialUnit su WHERE su.status = 'IN_STOCK'")
    List<SerialUnit> findAvailableSerialUnits();

    @Query("SELECT su FROM SerialUnit su WHERE su.sku.id = :skuId AND su.status = 'IN_STOCK'")
    List<SerialUnit> findAvailableBySkuId(@Param("skuId") Long skuId);

    boolean existsByImei(String imei);

    @Query("SELECT COUNT(su) FROM SerialUnit su WHERE su.status = :status")
    long countByStatus(@Param("status") SerialStatus status);
}
