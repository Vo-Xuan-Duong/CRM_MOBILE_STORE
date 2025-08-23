package com.example.Backend.services;

import com.example.Backend.dtos.campaign.CampaignRequest;
import com.example.Backend.dtos.campaign.CampaignResponse;
import com.example.Backend.exceptions.ResourceNotFoundException;
import com.example.Backend.models.Campaign;
import com.example.Backend.models.CampaignTarget;
import com.example.Backend.models.CampaignTargetId;
import com.example.Backend.models.Customer;
import com.example.Backend.models.User;
import com.example.Backend.repositorys.CampaignRepository;
import com.example.Backend.repositorys.CampaignTargetRepository;
import com.example.Backend.repositorys.CustomerRepository;
import com.example.Backend.repositorys.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignTargetRepository campaignTargetRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CampaignResponse createCampaign(CampaignRequest request, Long userId) {
        log.info("Creating campaign: {}", request.getName());

        User createdBy = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Campaign campaign = Campaign.builder()
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .budget(request.getBudget())
                .status(Campaign.CampaignStatus.DRAFT)
                .createdBy(createdBy)
                .build();

        Campaign savedCampaign = campaignRepository.save(campaign);
        log.info("Campaign created successfully with id: {}", savedCampaign.getId());

        return mapToResponse(savedCampaign);
    }

    public CampaignResponse updateCampaign(Long id, CampaignRequest request) {
        log.info("Updating campaign with id: {}", id);

        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));

        campaign.setName(request.getName());
        campaign.setType(request.getType());
        campaign.setDescription(request.getDescription());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        campaign.setBudget(request.getBudget());

        Campaign updatedCampaign = campaignRepository.save(campaign);
        log.info("Campaign updated successfully");

        return mapToResponse(updatedCampaign);
    }

    @Transactional(readOnly = true)
    public CampaignResponse getCampaignById(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        return mapToResponse(campaign);
    }

    @Transactional(readOnly = true)
    public Page<CampaignResponse> getAllCampaigns(Pageable pageable) {
        Page<Campaign> campaigns = campaignRepository.findAll(pageable);
        return campaigns.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<CampaignResponse> getCampaignsByStatus(String status) {
        Campaign.CampaignStatus campaignStatus = Campaign.CampaignStatus.valueOf(status.toUpperCase());
        List<Campaign> campaigns = campaignRepository.findByStatus(campaignStatus);
        return campaigns.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CampaignResponse> getCampaignsByType(String type) {
        Campaign.CampaignType campaignType = Campaign.CampaignType.valueOf(type.toUpperCase());
        List<Campaign> campaigns = campaignRepository.findByType(campaignType);
        return campaigns.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CampaignResponse> getActiveCampaigns() {
        List<Campaign> campaigns = campaignRepository.findByStatus(
                Campaign.CampaignStatus.ACTIVE);
        return campaigns.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void startCampaign(Long id) {
        log.info("Starting campaign with id: {}", id);

        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));

        if (campaign.getStatus() != Campaign.CampaignStatus.DRAFT) {
            throw new IllegalStateException("Only draft campaigns can be started");
        }

        campaign.setStatus(Campaign.CampaignStatus.ACTIVE);
        campaign.setStartDate(LocalDateTime.now().toLocalDate());
        campaignRepository.save(campaign);

        log.info("Campaign started successfully");
    }

    public void pauseCampaign(Long id) {
        log.info("Pausing campaign with id: {}", id);

        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));

        if (campaign.getStatus() != Campaign.CampaignStatus.ACTIVE) {
            throw new IllegalStateException("Only active campaigns can be paused");
        }

        campaign.setStatus(Campaign.CampaignStatus.PAUSED);
        campaignRepository.save(campaign);

        log.info("Campaign paused successfully");
    }

    public void completeCampaign(Long id) {
        log.info("Completing campaign with id: {}", id);

        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));

        campaign.setStatus(Campaign.CampaignStatus.COMPLETED);
        campaign.setEndDate(LocalDateTime.now().toLocalDate());
        campaignRepository.save(campaign);

        log.info("Campaign completed successfully");
    }

    public void cancelCampaign(Long id) {
        log.info("Cancelling campaign with id: {}", id);

        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));

        campaign.setStatus(Campaign.CampaignStatus.CANCELLED);
        campaignRepository.save(campaign);

        log.info("Campaign cancelled successfully");
    }

    public void addTargets(Long campaignId, List<Long> customerIds) {
        log.info("Adding {} targets to campaign {}", customerIds.size(), campaignId);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));

        for (Long customerId : customerIds) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

            CampaignTargetId targetId = new CampaignTargetId(campaignId, customerId);

            if (!campaignTargetRepository.existsById(targetId)) {
                CampaignTarget target = CampaignTarget.builder()
                        .campaign(campaign)
                        .customer(customer)
                        .build();

                campaignTargetRepository.save(target);
            }
        }

        log.info("Targets added successfully");
    }

    public void removeTarget(Long campaignId, Long customerId) {
        log.info("Removing target {} from campaign {}", customerId, campaignId);

        CampaignTargetId targetId = new CampaignTargetId(campaignId, customerId);

        if (campaignTargetRepository.existsById(targetId)) {
            campaignTargetRepository.deleteById(targetId);
            log.info("Target removed successfully");
        } else {
            throw new ResourceNotFoundException("Campaign target not found");
        }
    }

    @Transactional(readOnly = true)
    public List<Object> getCampaignTargets(Long campaignId) {
        campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));

        List<CampaignTarget> targets = campaignTargetRepository.findByCampaignId(campaignId);

        return targets.stream()
                .map(target -> {
                    Customer customer = target.getCustomer();
                    return CampaignTargetResponse.builder()
                            .customerId(customer.getId())
                            .customerName(customer.getFullName())
                            .customerPhone(customer.getPhone())
                            .customerEmail(customer.getEmail())
                            .customerTier(customer.getTier())
                            .addedAt(target.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private CampaignResponse mapToResponse(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .type(campaign.getType())
                .description(campaign.getDescription())
                .status(campaign.getStatus())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .budget(campaign.getBudget())
                .isActive(campaign.isActive())
                .createdById(campaign.getCreatedBy().getId())
                .createdByName(campaign.getCreatedBy().getFullName())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .build();
    }

    public CampaignStatistics getCampaignStatistics() {
        log.info("Fetching campaign statistics");

        long totalCampaigns = campaignRepository.count();
        long activeCampaigns = campaignRepository.countByStatus(Campaign.CampaignStatus.ACTIVE);
        long completedCampaigns = campaignRepository.countByStatus(Campaign.CampaignStatus.COMPLETED);
        long cancelledCampaigns = campaignRepository.countByStatus(Campaign.CampaignStatus.CANCELLED);

        return CampaignStatistics.builder()
                .totalCampaigns(totalCampaigns)
                .activeCampaigns(activeCampaigns)
                .completedCampaigns(completedCampaigns)
                .cancelledCampaigns(cancelledCampaigns)
                .build();
    }

    public Page<CampaignResponse> searchCampaigns(String keyword, Pageable pageable) {
        log.info("Searching campaigns with keyword: {}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCampaigns(pageable);
        }

        // Get all matching campaigns first, then apply pagination manually
        List<Campaign> allCampaigns = campaignRepository.searchCampaigns(keyword);

        // Convert to page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCampaigns.size());
        List<Campaign> pagedCampaigns = allCampaigns.subList(start, end);

        Page<Campaign> campaigns = new org.springframework.data.domain.PageImpl<>(
            pagedCampaigns,
            pageable,
            allCampaigns.size()
        );

        return campaigns.map(this::mapToResponse);
    }

    public CampaignPerformance getCampaignPerformance(Long id) {
        log.info("Fetching performance for campaign with id: {}", id);

        campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));

        // TODO: Implement actual performance metrics calculation
        // For now, return default values
        return CampaignPerformance.builder()
                .totalTargets(0L)
                .activeTargets(0L)
                .completedTargets(0L)
                .cancelledTargets(0L)
                .conversionRate(0.0)
                .totalRevenue(0.0)
                .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class CampaignTargetResponse {
        private Long customerId;
        private String customerName;
        private String customerPhone;
        private String customerEmail;
        private Object customerTier;
        private LocalDateTime addedAt;
    }

    @lombok.Data
    @lombok.Builder
    public static class CampaignStatistics {
        private long totalCampaigns;
        private long activeCampaigns;
        private long completedCampaigns;
        private long cancelledCampaigns;
    }

    @lombok.Data
    @lombok.Builder
    public static class CampaignPerformance {
        private long totalTargets;
        private long activeTargets;
        private long completedTargets;
        private long cancelledTargets;
        private double conversionRate;
        private double totalRevenue;
    }
}
