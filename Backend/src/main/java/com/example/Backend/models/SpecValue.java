package com.example.Backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "spec_value")
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
    @JoinColumn(name = "model_id")
    private ProductModel model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id")
    private SKU sku;

    @Column(name = "value_text", columnDefinition = "TEXT")
    private String valueText;

    @Column(name = "value_number", precision = 18, scale = 4)
    private BigDecimal valueNumber;

    @Column(name = "value_bool")
    private Boolean valueBool;

    @Column(name = "value_json", columnDefinition = "jsonb")
    private String valueJson;

    @Column(name = "unit_override")
    private String unitOverride;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Business logic methods
    public boolean belongsToModel() {
        return model != null && sku == null;
    }

    public boolean belongsToSku() {
        return sku != null && model == null;
    }

    public Object getValue() {
        if (field == null) return null;

        switch (field.getDataType()) {
            case TEXT:
                return valueText;
            case NUMBER:
                return valueNumber;
            case BOOLEAN:
                return valueBool;
            case JSON:
                return valueJson;
            default:
                return null;
        }
    }

    public String getDisplayValue() {
        Object value = getValue();
        if (value == null) return "";

        String unit = unitOverride != null ? unitOverride :
                     (field != null ? field.getUnit() : null);

        return value.toString() + (unit != null ? " " + unit : "");
    }
}
