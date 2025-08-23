package com.example.Backend.dtos.spec;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecValueRequest {

    @NotNull(message = "Field ID không được để trống")
    private Long fieldId;

    // Chỉ một trong hai ID này được set
    private Long productModelId;
    private Long skuId;

    @NotNull(message = "Giá trị không được để trống")
    private Object value;

    // Hoặc dùng map để batch update nhiều values
    private Map<Long, Object> fieldValues;
}
