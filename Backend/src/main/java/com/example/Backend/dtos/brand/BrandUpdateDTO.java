package com.example.Backend.dtos.brand;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BrandUpdateDTO {

    @Size(max = 255, message = "Tên thương hiệu không được vượt quá 255 ký tự")
    private String name;

    @Size(max = 100, message = "URL logo không được vượt quá 100 ký tự")
    private String logoUrl;

    @Size(max = 500, message = "Website không được vượt quá 500 ký tự")
    private String website;
}
