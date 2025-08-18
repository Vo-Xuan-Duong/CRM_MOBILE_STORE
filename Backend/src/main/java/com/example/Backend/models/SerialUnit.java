package com.example.Backend.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "serial_unit")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerialUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", nullable = false)
    private SKU sku;

    @NotBlank(message = "IMEI is required")
    @Column(nullable = false, unique = true)
    private String imei;

    @Column(name = "serial_number")
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SerialStatus status = SerialStatus.IN_STOCK;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum SerialStatus {
        IN_STOCK("in_stock"),
        SOLD("sold"),
        REPAIR("repair"),
        LOST("lost"),
        RETURNED("returned");

        private final String value;

        SerialStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Business logic methods
    public boolean isAvailable() {
        return status == SerialStatus.IN_STOCK;
    }

    public boolean isSold() {
        return status == SerialStatus.SOLD;
    }

    public boolean isInRepair() {
        return status == SerialStatus.REPAIR;
    }
}
