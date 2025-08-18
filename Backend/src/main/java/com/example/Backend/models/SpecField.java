package com.example.Backend.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum DataType {
        TEXT("text"),
        NUMBER("number"),
        BOOLEAN("boolean"),
        JSON("json");

        private final String value;

        DataType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum AppliesTo {
        MODEL("model"),
        SKU("sku");

        private final String value;

        AppliesTo(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
