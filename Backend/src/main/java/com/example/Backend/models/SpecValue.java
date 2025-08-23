package com.example.Backend.models;

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
@Table(name = "spec_value", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"field_id", "product_model_id"}),
    @UniqueConstraint(columnNames = {"field_id", "sku_id"})
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private SpecField field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_model_id")
    private ProductModel productModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id")
    private SKU sku;

    @Column(name = "text_value")
    private String textValue;

    @Column(name = "number_value")
    private Long numberValue;

    @Column(name = "decimal_value")
    private Double decimalValue;

    @Column(name = "boolean_value")
    private Boolean booleanValue;

    @Column(name = "date_value")
    private LocalDateTime dateValue;

    @Column(name = "select_value")
    private String selectValue;

    @Column(name = "multiselect_value", columnDefinition = "TEXT")
    private String multiselectValue; // JSON array of selected values

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper methods to get/set values based on field type
    public Object getValue() {
        if (field == null) return null;

        return switch (field.getDataType()) {
            case TEXT -> textValue;
            case NUMBER -> numberValue;
            case DECIMAL -> decimalValue;
            case BOOLEAN -> booleanValue;
            case DATE -> dateValue;
            case SELECT -> selectValue;
            case MULTISELECT -> multiselectValue;
        };
    }

    public void setValue(Object value) {
        if (field == null) return;

        // Clear all values first
        clearAllValues();

        // Set the appropriate value based on field type
        switch (field.getDataType()) {
            case TEXT -> textValue = (String) value;
            case NUMBER -> numberValue = (Long) value;
            case DECIMAL -> decimalValue = (Double) value;
            case BOOLEAN -> booleanValue = (Boolean) value;
            case DATE -> dateValue = (LocalDateTime) value;
            case SELECT -> selectValue = (String) value;
            case MULTISELECT -> multiselectValue = (String) value;
        }
    }

    private void clearAllValues() {
        textValue = null;
        numberValue = null;
        decimalValue = null;
        booleanValue = null;
        dateValue = null;
        selectValue = null;
        multiselectValue = null;
    }

    // Validation method
    public boolean isValidForTarget() {
        if (field == null) return false;

        SpecField.AppliesTo appliesTo = field.getAppliesTo();
        boolean hasProductModel = productModel != null;
        boolean hasSku = sku != null;

        return switch (appliesTo) {
            case MODEL -> hasProductModel && !hasSku;
            case SKU -> !hasProductModel && hasSku;
            case BOTH -> hasProductModel ^ hasSku; // XOR - exactly one should be set
        };
    }
}
