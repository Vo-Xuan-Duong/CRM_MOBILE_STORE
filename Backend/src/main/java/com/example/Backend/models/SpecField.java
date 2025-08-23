package com.example.Backend.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Data
@Entity
@Table(name = "spec_field", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"group_id", "field_key"})
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private SpecGroup group;

    @NotBlank(message = "Field key is required")
    @Column(name = "field_key", nullable = false)
    private String fieldKey;

    @NotBlank(message = "Label is required")
    @Column(nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    private DataType dataType;

    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to", nullable = false)
    @Builder.Default
    private AppliesTo appliesTo = AppliesTo.MODEL;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Getter
    public enum DataType {
        TEXT("text"),
        NUMBER("number"),
        DECIMAL("decimal"),
        BOOLEAN("boolean"),
        DATE("date"),
        SELECT("select"),
        MULTISELECT("multiselect");

        private final String value;

        DataType(String value) {
            this.value = value;
        }
    }

    @Getter
    public enum AppliesTo {
        MODEL("model"),
        SKU("sku"),
        BOTH("both");

        private final String value;

        AppliesTo(String value) {
            this.value = value;
        }
    }
}
