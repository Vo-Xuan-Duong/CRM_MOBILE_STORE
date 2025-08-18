package com.example.Backend.dtos.category;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoryResponseDTO {

    private Long id;
    private String name;
    private String description;

    // Thông tin parent category
    private Long parentId;
    private String parentName;

    // Đường dẫn đầy đủ (VD: "Điện thoại > Smartphone > iPhone")
    private String fullPath;

    // Cấp độ trong cây category (0 = root)
    private Integer level;

    // Số lượng category con
    private Long childrenCount;

    // Số lượng model trong category này
    private Long modelCount;

    // Danh sách category con (nếu có)
    private List<CategoryResponseDTO> children;

    // Timestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Có thể xóa được không
    private Boolean canDelete;
}
