package com.example.Backend.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "warranty")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warranty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false, unique = true)
    private SalesOrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_unit_id")
    private SerialUnit serialUnit;

    @NotBlank(message = "Warranty code is required")
    @Column(name = "warranty_code", nullable = false, unique = true)
    private String warrantyCode;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Min(value = 1, message = "Warranty months must be positive")
    @Column(nullable = false)
    @Builder.Default
    private Integer months = 12;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private WarrantyStatus status = WarrantyStatus.ACTIVE;

    @Column(name = "qr_image_url")
    private String qrImageUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum WarrantyStatus {
        ACTIVE("active"),
        EXPIRED("expired"),
        VOID("void"),
        CLAIMED("claimed");

        private final String value;

        WarrantyStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Business logic methods
    public boolean isActive() {
        return status == WarrantyStatus.ACTIVE && !isExpired();
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    public boolean canClaim() {
        return isActive();
    }

    public long getDaysRemaining() {
        if (isExpired()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }
}
