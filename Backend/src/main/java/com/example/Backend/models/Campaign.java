package com.example.Backend.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "campaign")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Campaign name is required")
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 12, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CampaignStatus status = CampaignStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Getter
    public enum CampaignType {
        EMAIL("email"),
        SMS("sms"),
        CALL("call"),
        PROMOTION("promotion"),
        EVENT("event");

        private final String value;

        CampaignType(String value) {
            this.value = value;
        }

    }

    @Getter
    public enum CampaignStatus {
        DRAFT("draft"),
        ACTIVE("active"),
        PAUSED("paused"),
        COMPLETED("completed"),
        CANCELLED("cancelled");

        private final String value;

        CampaignStatus(String value) {
            this.value = value;
        }

    }

    // Business logic methods
    public boolean isActive() {
        return status == CampaignStatus.ACTIVE;
    }

    public boolean canStart() {
        return status == CampaignStatus.DRAFT;
    }

    public boolean canPause() {
        return status == CampaignStatus.ACTIVE;
    }

    public boolean isRunning() {
        LocalDate now = LocalDate.now();
        return status == CampaignStatus.ACTIVE &&
               (startDate == null || !startDate.isAfter(now)) &&
               (endDate == null || !endDate.isBefore(now));
    }
}
