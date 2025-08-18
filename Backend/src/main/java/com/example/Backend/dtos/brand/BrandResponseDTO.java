package com.example.Backend.dtos.brand;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BrandResponseDTO {

    private Long id;
    private String name;
    private String country;
    private String website;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Thêm thông tin thống kê nếu cần
    private Long productCount;
    private Long modelCount;
}
