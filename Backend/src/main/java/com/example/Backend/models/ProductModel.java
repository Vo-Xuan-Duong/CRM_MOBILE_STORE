package com.example.Backend.models;

import java.time.LocalDateTime;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "product_model", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"brand_id", "name"})
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @NotBlank(message = "Product model name is required")
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductCategory category = ProductCategory.PHONE;

    @Min(value = 0, message = "Warranty months must be non-negative")
    @Column(name = "default_warranty_months", nullable = false)
    @Builder.Default
    private Integer defaultWarrantyMonths = 12;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Getter
    public enum ProductCategory {
        PHONE("phone"),
        ACCESSORY("accessory"),
        SERVICE("service");

        private final String value;

        ProductCategory(String value) {
            this.value = value;
        }

    }
}
