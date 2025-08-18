package com.example.Backend.repositorys;

import com.example.Backend.models.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByStatus(Campaign.CampaignStatus status);

    List<Campaign> findByType(Campaign.CampaignType type);

    List<Campaign> findByStatusAndIsActive(Campaign.CampaignStatus status, Boolean isActive);

    List<Campaign> findByCreatedById(Long createdById);

    @Query("SELECT c FROM Campaign c WHERE c.startDate <= :date AND c.endDate >= :date AND c.isActive = true")
    List<Campaign> findActiveCampaignsOnDate(@Param("date") LocalDate date);

    @Query("SELECT c FROM Campaign c WHERE c.startDate BETWEEN :startDate AND :endDate")
    List<Campaign> findCampaignsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(c) FROM Campaign c WHERE c.status = :status")
    long countByStatus(@Param("status") Campaign.CampaignStatus status);

    @Query("SELECT c FROM Campaign c WHERE c.name LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Campaign> searchCampaigns(@Param("keyword") String keyword);
}
