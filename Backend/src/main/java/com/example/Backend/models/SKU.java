package com.example.Backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "sku", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"model_id", "variant_name", "color", "storage_gb"})
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SKU {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private ProductModel model;

    @Column(name = "variant_name")
    private String variantName;

    private String color;

    @Column(name = "storage_gb")
    private Integer storageGb;

    @Column(name = "ram_gb")
    private Integer ramGb;

    @Column(unique = true)
    private String barcode;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost price must be non-negative")
    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "is_serialized", nullable = false)
    @Builder.Default
    private Boolean isSerialized = true;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
