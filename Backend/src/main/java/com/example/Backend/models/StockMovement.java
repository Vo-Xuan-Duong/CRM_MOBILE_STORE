package com.example.Backend.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "stock_movement")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", nullable = false)
    private SKU sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_unit_id")
    private SerialUnit serialUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Min(value = 1, message = "Quantity must be positive")
    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementReason reason;

    @Column(name = "ref_type")
    private String refType;

    @Column(name = "ref_id")
    private Long refId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum MovementType {
        IN("in"),
        OUT("out");

        private final String value;

        MovementType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum MovementReason {
        PURCHASE("purchase"),
        SALE("sale"),
        RETURN("return"),
        REPAIR("repair"),
        ADJUSTMENT("adjustment"),
        TRANSFER("transfer"),
        DAMAGED("damaged");

        private final String value;

        MovementReason(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Business logic methods
    public boolean isInbound() {
        return movementType == MovementType.IN;
    }

    public boolean isOutbound() {
        return movementType == MovementType.OUT;
    }

    public Integer getSignedQuantity() {
        return isInbound() ? quantity : -quantity;
    }
}
