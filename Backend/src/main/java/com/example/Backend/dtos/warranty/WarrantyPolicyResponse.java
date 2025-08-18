package com.example.Backend.dtos.warranty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyPolicyResponse {

    private UUID id;
    private Integer months;
    private String conditions;

    // Related entities
    private BrandInfo brand;
    private ModelInfo model;
    private VariantInfo variant;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandInfo {
        private UUID id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelInfo {
        private UUID id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantInfo {
        private UUID id;
        private String color;
        private Integer ramGb;
        private Integer storageGb;
    }
}
