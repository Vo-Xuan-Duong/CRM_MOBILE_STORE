package com.example.Backend.dtos.media;

import com.example.Backend.models.ProductMedia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMediaRequest {

    private Long modelId;

    private Long skuId;

    @NotNull(message = "Media type is required")
    private ProductMedia.MediaType mediaType;

    @NotBlank(message = "URL is required")
    private String url;

    private String caption;

    private Integer sortOrder = 0;

    private Boolean isPrimary = false;
}
