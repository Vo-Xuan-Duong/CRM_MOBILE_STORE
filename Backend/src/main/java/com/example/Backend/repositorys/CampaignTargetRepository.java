package com.example.Backend.repositorys;

import com.example.Backend.models.CampaignTarget;
import com.example.Backend.models.CampaignTargetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignTargetRepository extends JpaRepository<CampaignTarget, CampaignTargetId> {

    List<CampaignTarget> findByCampaignId(Long campaignId);

    List<CampaignTarget> findByCustomerId(Long customerId);

    @Query("SELECT ct FROM CampaignTarget ct WHERE ct.campaign.id = :campaignId AND ct.customer.tier = :tier")
    List<CampaignTarget> findByCampaignIdAndCustomerTier(@Param("campaignId") Long campaignId, @Param("tier") Object tier);

    @Query("SELECT COUNT(ct) FROM CampaignTarget ct WHERE ct.campaign.id = :campaignId")
    long countByCampaignId(@Param("campaignId") Long campaignId);

    boolean existsByCampaignIdAndCustomerId(Long campaignId, Long customerId);
}
