package com.example.Backend.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "campaign_target")
@IdClass(CampaignTargetId.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignTarget {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CampaignTargetStatus status = CampaignTargetStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String response;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Getter
    public enum CampaignTargetStatus {
        PENDING("pending"),
        SENT("sent"),
        DELIVERED("delivered"),
        OPENED("opened"),
        CLICKED("clicked"),
        FAILED("failed"),
        UNSUBSCRIBED("unsubscribed");

        private final String value;

        CampaignTargetStatus(String value) {
            this.value = value;
        }
    }

    // Business logic methods
    public boolean isSent() {
        return sentAt != null;
    }

    public boolean isSuccessful() {
        return status == CampaignTargetStatus.DELIVERED ||
               status == CampaignTargetStatus.OPENED ||
               status == CampaignTargetStatus.CLICKED;
    }

    public boolean isFailed() {
        return status == CampaignTargetStatus.FAILED ||
               status == CampaignTargetStatus.UNSUBSCRIBED;
    }

    public boolean isPending() {
        return status == CampaignTargetStatus.PENDING;
    }

    public void markAsSent() {
        this.status = CampaignTargetStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markAsDelivered() {
        this.status = CampaignTargetStatus.DELIVERED;
    }

    public void markAsOpened() {
        this.status = CampaignTargetStatus.OPENED;
    }

    public void markAsClicked() {
        this.status = CampaignTargetStatus.CLICKED;
    }

    public void markAsFailed(String reason) {
        this.status = CampaignTargetStatus.FAILED;
        this.response = reason;
    }

    public void markAsUnsubscribed() {
        this.status = CampaignTargetStatus.UNSUBSCRIBED;
        this.response = "Customer unsubscribed";
    }
}
