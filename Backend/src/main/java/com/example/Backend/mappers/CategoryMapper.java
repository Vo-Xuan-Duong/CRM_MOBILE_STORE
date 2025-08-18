package com.example.Backend.mappers;

import com.example.Backend.dtos.category.*;
import com.example.Backend.models.Category;
import com.example.Backend.repositorys.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    private final CategoryRepository categoryRepository;

    /**
     * Chuyển đổi từ CategoryCreateDTO sang Category entity
     */
    public Category toEntity(CategoryCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        return Category.builder()
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .build();
    }

    /**
     * Chuyển đổi từ Category entity sang CategoryResponseDTO
     */
    public CategoryResponseDTO toResponseDTO(Category category) {
        return toResponseDTO(category, false);
    }

    /**
     * Chuyển đổi từ Category entity sang CategoryResponseDTO với tùy chọn include children
     */
    public CategoryResponseDTO toResponseDTO(Category category, boolean includeChildren) {
        if (category == null) {
            return null;
        }

        CategoryResponseDTO.CategoryResponseDTOBuilder builder = CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt());

        // Thông tin parent
        if (category.getParent() != null) {
            builder.parentId(category.getParent().getId())
                   .parentName(category.getParent().getName());
        }

        // Đường dẫn đầy đủ
        String fullPath = buildFullPath(category);
        builder.fullPath(fullPath);

        // Cấp độ
        Integer level = calculateLevel(category);
        builder.level(level);

        // Số lượng children
        long childrenCount = categoryRepository.findByParentId(category.getId()).size();
        builder.childrenCount(childrenCount);

        // Số lượng model
        long modelCount = categoryRepository.countModelsByCategory(category.getId());
        builder.modelCount(modelCount);

        // Có thể xóa không
        boolean canDelete = categoryRepository.canDeleteCategory(category.getId());
        builder.canDelete(canDelete);

        // Include children nếu được yêu cầu
        if (includeChildren) {
            List<Category> children = categoryRepository.findByParentId(category.getId());
            List<CategoryResponseDTO> childrenDTOs = children.stream()
                    .map(child -> toResponseDTO(child, false)) // Không đệ quy quá sâu
                    .collect(Collectors.toList());
            builder.children(childrenDTOs);
        }

        return builder.build();
    }

    /**
     * Cập nhật Category entity từ CategoryUpdateDTO
     */
    public void updateEntityFromDTO(Category category, CategoryUpdateDTO updateDTO) {
        if (category == null || updateDTO == null) {
            return;
        }

        if (updateDTO.getName() != null) {
            category.setName(updateDTO.getName());
        }

        if (updateDTO.getDescription() != null) {
            category.setDescription(updateDTO.getDescription());
        }
    }

    /**
     * Chuyển đổi danh sách Category entities sang danh sách CategoryResponseDTO
     */
    public List<CategoryResponseDTO> toResponseDTOList(List<Category> categories) {
        return toResponseDTOList(categories, false);
    }

    /**
     * Chuyển đổi danh sách Category entities sang danh sách CategoryResponseDTO với tùy chọn include children
     */
    public List<CategoryResponseDTO> toResponseDTOList(List<Category> categories, boolean includeChildren) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(category -> toResponseDTO(category, includeChildren))
                .collect(Collectors.toList());
    }

    /**
     * Tạo CategoryResponseDTO tóm tắt (chỉ thông tin cơ bản)
     */
    public CategoryResponseDTO toSummaryResponseDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryResponseDTO.CategoryResponseDTOBuilder builder = CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName());

        // Thông tin parent cơ bản
        if (category.getParent() != null) {
            builder.parentId(category.getParent().getId())
                   .parentName(category.getParent().getName());
        }

        // Đường dẫn và level
        builder.fullPath(buildFullPath(category))
               .level(calculateLevel(category));

        return builder.build();
    }

    /**
     * Tạo cây category hierarchy
     */
    public List<CategoryResponseDTO> buildCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        return rootCategories.stream()
                .map(category -> toResponseDTO(category, true))
                .collect(Collectors.toList());
    }

    /**
     * Tạo danh sách category flat với hierarchy path
     */
    public List<CategoryResponseDTO> buildFlatCategoryList() {
        List<Category> allCategories = categoryRepository.findAll();
        return allCategories.stream()
                .map(this::toResponseDTO)
                .sorted((c1, c2) -> c1.getFullPath().compareToIgnoreCase(c2.getFullPath()))
                .collect(Collectors.toList());
    }

    // Private helper methods

    /**
     * Xây dựng đường dẫn đầy đủ từ root đến category hiện tại
     */
    private String buildFullPath(Category category) {
        if (category == null) {
            return "";
        }

        if (category.getParent() == null) {
            return category.getName();
        }

        return buildFullPath(category.getParent()) + " > " + category.getName();
    }

    /**
     * Tính cấp độ của category (root = 0)
     */
    private Integer calculateLevel(Category category) {
        if (category == null || category.getParent() == null) {
            return 0;
        }

        return calculateLevel(category.getParent()) + 1;
    }

    /**
     * Kiểm tra xem category có phải là ancestor của target category không
     */
    public boolean isAncestor(Category category, Category target) {
        if (target == null || target.getParent() == null) {
            return false;
        }

        if (target.getParent().getId().equals(category.getId())) {
            return true;
        }

        return isAncestor(category, target.getParent());
    }

    /**
     * Kiểm tra xem việc di chuyển category có tạo ra chu trình không
     */
    public boolean wouldCreateCycle(Category categoryToMove, Category newParent) {
        if (newParent == null) {
            return false; // Di chuyển lên root không tạo chu trình
        }

        return categoryToMove.getId().equals(newParent.getId()) ||
               isAncestor(categoryToMove, newParent);
    }
}
