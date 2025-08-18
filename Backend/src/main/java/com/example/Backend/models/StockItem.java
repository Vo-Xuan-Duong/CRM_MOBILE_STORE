package com.example.Backend.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "stock_item")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", nullable = false, unique = true)
    private SKU sku;

    @Min(value = 0, message = "Quantity must be non-negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Min(value = 0, message = "Reserved quantity must be non-negative")
    @Column(name = "reserved_qty", nullable = false)
    @Builder.Default
    private Integer reservedQty = 0;

    @Min(value = 0, message = "Min stock must be non-negative")
    @Column(name = "min_stock", nullable = false)
    @Builder.Default
    private Integer minStock = 0;

    @Column(name = "max_stock")
    private Integer maxStock;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Business logic methods
    public Integer getAvailableQuantity() {
        return quantity - reservedQty;
    }

    public boolean isLowStock() {
        return quantity <= minStock;
    }

    public boolean canReserve(Integer requestedQty) {
        return getAvailableQuantity() >= requestedQty;
    }
}
