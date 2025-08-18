package com.example.Backend.dtos.category;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategorySearchRequest {

    private String keyword;
    private Long parentId; // Tìm trong một parent cụ thể
    private Boolean hasProducts; // Chỉ lấy category có/không có sản phẩm
    private Integer level; // Tìm theo cấp độ (0 = root, 1 = level 1, etc.)

    // Phân trang
    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 10;

    @Builder.Default
    private String sortBy = "name";

    @Builder.Default
    private String sortDirection = "ASC";
}
