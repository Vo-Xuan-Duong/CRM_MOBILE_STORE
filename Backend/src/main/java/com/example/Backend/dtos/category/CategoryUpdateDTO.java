package com.example.Backend.dtos.category;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoryUpdateDTO {

    @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự")
    private String name;

    private Long parentId; // Null để đặt thành root category

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;
}
