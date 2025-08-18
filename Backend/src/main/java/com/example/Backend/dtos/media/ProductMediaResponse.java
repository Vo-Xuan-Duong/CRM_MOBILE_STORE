package com.example.Backend.dtos.media;

import com.example.Backend.models.ProductMedia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMediaResponse {

    private Long id;
    private Long modelId;
    private String modelName;
    private Long skuId;
    private String skuName;
    private ProductMedia.MediaType mediaType;
    private String url;
    private String caption;
    private Integer sortOrder;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}
