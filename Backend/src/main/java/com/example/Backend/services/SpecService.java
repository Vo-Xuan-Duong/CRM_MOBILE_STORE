package com.example.Backend.services;

import com.example.Backend.dtos.spec.*;
import com.example.Backend.exceptions.SpecException;
import com.example.Backend.models.*;
import com.example.Backend.repositorys.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SpecService {

    private final SpecGroupRepository specGroupRepository;
    private final SpecFieldRepository specFieldRepository;
    private final SpecValueRepository specValueRepository;
    private final ProductModelRepository productModelRepository;
    private final SKURepository skuRepository;

    // ==================== SPEC GROUP MANAGEMENT ====================

    /**
     * Tạo nhóm spec mới
     */
    public SpecGroupResponse createSpecGroup(SpecGroupRequest request) {
        log.info("Creating new spec group: {}", request.getName());

        // Kiểm tra tên đã tồn tại
        if (specGroupRepository.existsByName(request.getName())) {
            throw new SpecException("Tên nhóm spec đã tồn tại: " + request.getName());
        }

        SpecGroup specGroup = SpecGroup.builder()
                .name(request.getName())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(true)
                .build();

        SpecGroup saved = specGroupRepository.save(specGroup);
        log.info("Spec group created successfully with ID: {}", saved.getId());

        return mapToSpecGroupResponse(saved);
    }

    /**
     * Cập nhật nhóm spec
     */
    public SpecGroupResponse updateSpecGroup(Long groupId, SpecGroupRequest request) {
        log.info("Updating spec group with ID: {}", groupId);

        SpecGroup specGroup = specGroupRepository.findById(groupId)
                .orElseThrow(() -> new SpecException("Không tìm thấy nhóm spec với ID: " + groupId));

        // Kiểm tra tên (nếu thay đổi)
        if (request.getName() != null && !request.getName().equals(specGroup.getName())) {
            if (specGroupRepository.existsByNameAndIdNot(request.getName(), groupId)) {
                throw new SpecException("Tên nhóm spec đã tồn tại: " + request.getName());
            }
            specGroup.setName(request.getName());
        }

        if (request.getSortOrder() != null) {
            specGroup.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            specGroup.setIsActive(request.getIsActive());
        }

        SpecGroup saved = specGroupRepository.save(specGroup);
        log.info("Spec group updated successfully with ID: {}", saved.getId());

        return mapToSpecGroupResponse(saved);
    }

    /**
     * Lấy tất cả nhóm spec active
     */
    @Transactional(readOnly = true)
    public List<SpecGroupResponse> getAllActiveSpecGroups() {
        return specGroupRepository.findByIsActiveTrueOrderBySortOrder()
                .stream()
                .map(this::mapToSpecGroupResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy nhóm spec theo ID
     */
    @Transactional(readOnly = true)
    public SpecGroupResponse getSpecGroupById(Long groupId) {
        SpecGroup specGroup = specGroupRepository.findById(groupId)
                .orElseThrow(() -> new SpecException("Không tìm thấy nhóm spec với ID: " + groupId));
        return mapToSpecGroupResponse(specGroup);
    }

    // ==================== SPEC FIELD MANAGEMENT ====================

    /**
     * Tạo field spec mới
     */
    public SpecFieldResponse createSpecField(SpecFieldRequest request) {
        log.info("Creating new spec field: {} for group: {}", request.getFieldKey(), request.getGroupId());

        SpecGroup group = specGroupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new SpecException("Không tìm thấy nhóm spec với ID: " + request.getGroupId()));

        // Kiểm tra field key đã tồn tại trong group
        if (specFieldRepository.existsByGroupIdAndFieldKey(request.getGroupId(), request.getFieldKey())) {
            throw new SpecException("Field key đã tồn tại trong nhóm: " + request.getFieldKey());
        }

        SpecField specField = SpecField.builder()
                .group(group)
                .fieldKey(request.getFieldKey())
                .label(request.getLabel())
                .dataType(request.getDataType())
                .unit(request.getUnit())
                .appliesTo(request.getAppliesTo() != null ? request.getAppliesTo() : SpecField.AppliesTo.MODEL)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isRequired(request.getIsRequired() != null ? request.getIsRequired() : false)
                .isActive(true)
                .build();

        SpecField saved = specFieldRepository.save(specField);
        log.info("Spec field created successfully with ID: {}", saved.getId());

        return mapToSpecFieldResponse(saved);
    }

    /**
     * Cập nhật field spec
     */
    public SpecFieldResponse updateSpecField(Long fieldId, SpecFieldRequest request) {
        log.info("Updating spec field with ID: {}", fieldId);

        SpecField specField = specFieldRepository.findById(fieldId)
                .orElseThrow(() -> new SpecException("Không tìm thấy spec field với ID: " + fieldId));

        // Kiểm tra field key (nếu thay đổi)
        if (request.getFieldKey() != null && !request.getFieldKey().equals(specField.getFieldKey())) {
            if (specFieldRepository.existsByGroupIdAndFieldKeyAndIdNot(
                    specField.getGroup().getId(), request.getFieldKey(), fieldId)) {
                throw new SpecException("Field key đã tồn tại trong nhóm: " + request.getFieldKey());
            }
            specField.setFieldKey(request.getFieldKey());
        }

        // Cập nhật các trường khác
        if (request.getLabel() != null) specField.setLabel(request.getLabel());
        if (request.getDataType() != null) specField.setDataType(request.getDataType());
        if (request.getUnit() != null) specField.setUnit(request.getUnit());
        if (request.getAppliesTo() != null) specField.setAppliesTo(request.getAppliesTo());
        if (request.getSortOrder() != null) specField.setSortOrder(request.getSortOrder());
        if (request.getIsRequired() != null) specField.setIsRequired(request.getIsRequired());
        if (request.getIsActive() != null) specField.setIsActive(request.getIsActive());

        SpecField saved = specFieldRepository.save(specField);
        log.info("Spec field updated successfully with ID: {}", saved.getId());

        return mapToSpecFieldResponse(saved);
    }

    /**
     * Lấy tất cả fields của một group
     */
    @Transactional(readOnly = true)
    public List<SpecFieldResponse> getSpecFieldsByGroup(Long groupId) {
        return specFieldRepository.findByGroupIdAndIsActiveTrueOrderBySortOrder(groupId)
                .stream()
                .map(this::mapToSpecFieldResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy spec field theo ID
     */
    @Transactional(readOnly = true)
    public SpecFieldResponse getSpecFieldById(Long fieldId) {
        SpecField specField = specFieldRepository.findById(fieldId)
                .orElseThrow(() -> new SpecException("Không tìm thấy spec field với ID: " + fieldId));
        return mapToSpecFieldResponse(specField);
    }

    // ==================== SPEC VALUE MANAGEMENT ====================

    /**
     * Lưu/cập nhật spec values cho ProductModel
     */
    public List<SpecValueResponse> saveSpecValuesForProductModel(Long productModelId, Map<Long, Object> fieldValues) {
        log.info("Saving spec values for ProductModel ID: {}", productModelId);

        ProductModel productModel = productModelRepository.findById(productModelId)
                .orElseThrow(() -> new SpecException("Không tìm thấy ProductModel với ID: " + productModelId));

        List<SpecValue> savedValues = fieldValues.entrySet().stream().map(entry -> {
            Long fieldId = entry.getKey();
            Object value = entry.getValue();

            SpecField field = specFieldRepository.findById(fieldId)
                    .orElseThrow(() -> new SpecException("Không tìm thấy spec field với ID: " + fieldId));

            // Kiểm tra field có áp dụng cho ProductModel không
            if (field.getAppliesTo() == SpecField.AppliesTo.SKU) {
                throw new SpecException("Field này chỉ áp dụng cho SKU: " + field.getLabel());
            }

            // Tìm hoặc tạo mới SpecValue
            SpecValue specValue = specValueRepository.findByFieldIdAndProductModelId(fieldId, productModelId)
                    .orElse(SpecValue.builder()
                            .field(field)
                            .productModel(productModel)
                            .build());

            specValue.setValue(value);
            return specValueRepository.save(specValue);
        }).collect(Collectors.toList());

        log.info("Spec values saved successfully for ProductModel ID: {}", productModelId);

        return savedValues.stream()
                .map(this::mapToSpecValueResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lưu/cập nhật spec values cho SKU
     */
    public List<SpecValueResponse> saveSpecValuesForSku(Long skuId, Map<Long, Object> fieldValues) {
        log.info("Saving spec values for SKU ID: {}", skuId);

        SKU sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new SpecException("Không tìm thấy SKU với ID: " + skuId));

        List<SpecValue> savedValues = fieldValues.entrySet().stream().map(entry -> {
            Long fieldId = entry.getKey();
            Object value = entry.getValue();

            SpecField field = specFieldRepository.findById(fieldId)
                    .orElseThrow(() -> new SpecException("Không tìm thấy spec field với ID: " + fieldId));

            // Kiểm tra field có áp dụng cho SKU không
            if (field.getAppliesTo() == SpecField.AppliesTo.MODEL) {
                throw new SpecException("Field này chỉ áp dụng cho ProductModel: " + field.getLabel());
            }

            // Tìm hoặc tạo mới SpecValue
            SpecValue specValue = specValueRepository.findByFieldIdAndSkuId(fieldId, skuId)
                    .orElse(SpecValue.builder()
                            .field(field)
                            .sku(sku)
                            .build());

            specValue.setValue(value);
            return specValueRepository.save(specValue);
        }).collect(Collectors.toList());

        log.info("Spec values saved successfully for SKU ID: {}", skuId);

        return savedValues.stream()
                .map(this::mapToSpecValueResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả spec values cho ProductModel
     */
    @Transactional(readOnly = true)
    public List<SpecValueResponse> getSpecValuesForProductModel(Long productModelId) {
        return specValueRepository.findAllByProductModelIdWithGroupAndField(productModelId)
                .stream()
                .map(this::mapToSpecValueResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả spec values cho SKU
     */
    @Transactional(readOnly = true)
    public List<SpecValueResponse> getSpecValuesForSku(Long skuId) {
        return specValueRepository.findAllBySkuIdWithGroupAndField(skuId)
                .stream()
                .map(this::mapToSpecValueResponse)
                .collect(Collectors.toList());
    }

    /**
     * Xóa spec value
     */
    public void deleteSpecValue(Long specValueId) {
        log.info("Deleting spec value with ID: {}", specValueId);

        if (!specValueRepository.existsById(specValueId)) {
            throw new SpecException("Không tìm thấy spec value với ID: " + specValueId);
        }

        specValueRepository.deleteById(specValueId);
        log.info("Spec value deleted successfully with ID: {}", specValueId);
    }

    // ==================== ADDITIONAL METHODS FOR NEW APIS ====================

    /**
     * Kích hoạt spec group
     */
    public void activateSpecGroup(Long groupId) {
        log.info("Activating spec group with ID: {}", groupId);

        SpecGroup specGroup = specGroupRepository.findById(groupId)
                .orElseThrow(() -> new SpecException("Không tìm thấy nhóm spec với ID: " + groupId));

        specGroup.setIsActive(true);
        specGroupRepository.save(specGroup);
        log.info("Spec group activated successfully with ID: {}", groupId);
    }

    /**
     * Vô hiệu hóa spec group
     */
    public void deactivateSpecGroup(Long groupId) {
        log.info("Deactivating spec group with ID: {}", groupId);

        SpecGroup specGroup = specGroupRepository.findById(groupId)
                .orElseThrow(() -> new SpecException("Không tìm thấy nhóm spec với ID: " + groupId));

        specGroup.setIsActive(false);
        specGroupRepository.save(specGroup);
        log.info("Spec group deactivated successfully with ID: {}", groupId);
    }

    /**
     * Lấy danh sách spec groups bị vô hiệu hóa
     */
    @Transactional(readOnly = true)
    public List<SpecGroupResponse> getInactiveSpecGroups() {
        return specGroupRepository.findByIsActiveFalse()
                .stream()
                .map(this::mapToSpecGroupResponse)
                .collect(Collectors.toList());
    }


    /**
     * Kích hoạt spec field
     */
    public void activateSpecField(Long fieldId) {
        log.info("Activating spec field with ID: {}", fieldId);

        SpecField specField = specFieldRepository.findById(fieldId)
                .orElseThrow(() -> new SpecException("Không tìm thấy spec field với ID: " + fieldId));

        specField.setIsActive(true);
        specFieldRepository.save(specField);
        log.info("Spec field activated successfully with ID: {}", fieldId);
    }

    /**
     * Vô hiệu hóa spec field
     */
    public void deactivateSpecField(Long fieldId) {
        log.info("Deactivating spec field with ID: {}", fieldId);

        SpecField specField = specFieldRepository.findById(fieldId)
                .orElseThrow(() -> new SpecException("Không tìm thấy spec field với ID: " + fieldId));

        specField.setIsActive(false);
        specFieldRepository.save(specField);
        log.info("Spec field deactivated successfully with ID: {}", fieldId);
    }

    /**
     * Lấy danh sách spec fields bị vô hiệu hóa
     */
    @Transactional(readOnly = true)
    public Page<SpecFieldResponse> getInactiveSpecFields(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("group.sortOrder", "sortOrder"));
        return specFieldRepository.findByIsActiveFalse(pageable)
                .map(this::mapToSpecFieldResponse);
    }

    /**
     * Lấy spec fields theo AppliesTo
     */
    @Transactional(readOnly = true)
    public List<SpecFieldResponse> getSpecFieldsByAppliesTo(SpecField.AppliesTo appliesTo) {
        return specFieldRepository.findByAppliesToAndIsActiveTrueOrderByGroupIdAscSortOrderAsc(appliesTo)
                .stream()
                .map(this::mapToSpecFieldResponse)
                .collect(Collectors.toList());
    }

    /**
     * Kích hoạt hàng loạt spec groups
     */
    public void bulkActivateSpecGroups(List<Long> groupIds) {
        log.info("Bulk activating spec groups: {}", groupIds);

        List<SpecGroup> specGroups = specGroupRepository.findAllById(groupIds);
        specGroups.forEach(group -> group.setIsActive(true));
        specGroupRepository.saveAll(specGroups);

        log.info("Bulk activated {} spec groups", specGroups.size());
    }

    /**
     * Vô hiệu hóa hàng loạt spec groups
     */
    public void bulkDeactivateSpecGroups(List<Long> groupIds) {
        log.info("Bulk deactivating spec groups: {}", groupIds);

        List<SpecGroup> specGroups = specGroupRepository.findAllById(groupIds);
        specGroups.forEach(group -> group.setIsActive(false));
        specGroupRepository.saveAll(specGroups);

        log.info("Bulk deactivated {} spec groups", specGroups.size());
    }

    /**
     * Kích hoạt hàng loạt spec fields
     */
    public void bulkActivateSpecFields(List<Long> fieldIds) {
        log.info("Bulk activating spec fields: {}", fieldIds);

        List<SpecField> specFields = specFieldRepository.findAllById(fieldIds);
        specFields.forEach(field -> field.setIsActive(true));
        specFieldRepository.saveAll(specFields);

        log.info("Bulk activated {} spec fields", specFields.size());
    }

    /**
     * Vô hiệu hóa hàng loạt spec fields
     */
    public void bulkDeactivateSpecFields(List<Long> fieldIds) {
        log.info("Bulk deactivating spec fields: {}", fieldIds);

        List<SpecField> specFields = specFieldRepository.findAllById(fieldIds);
        specFields.forEach(field -> field.setIsActive(false));
        specFieldRepository.saveAll(specFields);

        log.info("Bulk deactivated {} spec fields", specFields.size());
    }

    /**
     * Sao chép spec group với tất cả fields
     */
    public SpecGroupResponse cloneSpecGroup(Long groupId, String newName) {
        log.info("Cloning spec group ID: {} with new name: {}", groupId, newName);

        SpecGroup originalGroup = specGroupRepository.findById(groupId)
                .orElseThrow(() -> new SpecException("Không tìm thấy nhóm spec với ID: " + groupId));

        // Kiểm tra tên mới đã tồn tại chưa
        if (specGroupRepository.existsByName(newName)) {
            throw new SpecException("Tên nhóm spec đã tồn tại: " + newName);
        }

        // Tạo group mới
        SpecGroup newGroup = SpecGroup.builder()
                .name(newName)
                .sortOrder(originalGroup.getSortOrder())
                .isActive(true)
                .build();

        SpecGroup savedGroup = specGroupRepository.save(newGroup);

        // Sao chép tất cả fields
        List<SpecField> originalFields = specFieldRepository.findByGroupIdAndIsActiveTrueOrderBySortOrder(groupId);
        for (SpecField originalField : originalFields) {
            SpecField newField = SpecField.builder()
                    .group(savedGroup)
                    .fieldKey(originalField.getFieldKey())
                    .label(originalField.getLabel())
                    .dataType(originalField.getDataType())
                    .unit(originalField.getUnit())
                    .appliesTo(originalField.getAppliesTo())
                    .sortOrder(originalField.getSortOrder())
                    .isRequired(originalField.getIsRequired())
                    .isActive(true)
                    .build();

            specFieldRepository.save(newField);
        }

        log.info("Spec group cloned successfully. New ID: {}", savedGroup.getId());
        return mapToSpecGroupResponse(savedGroup);
    }

    /**
     * Sao chép spec field sang group khác
     */
    public SpecFieldResponse copySpecFieldToGroup(Long fieldId, Long targetGroupId) {
        log.info("Copying spec field ID: {} to group ID: {}", fieldId, targetGroupId);

        SpecField originalField = specFieldRepository.findById(fieldId)
                .orElseThrow(() -> new SpecException("Không tìm thấy spec field với ID: " + fieldId));

        SpecGroup targetGroup = specGroupRepository.findById(targetGroupId)
                .orElseThrow(() -> new SpecException("Không tìm thấy nhóm spec với ID: " + targetGroupId));

        // Kiểm tra field key đã tồn tại trong target group chưa
        if (specFieldRepository.existsByGroupIdAndFieldKey(targetGroupId, originalField.getFieldKey())) {
            throw new SpecException("Field key đã tồn tại trong nhóm đích: " + originalField.getFieldKey());
        }

        // Tạo field mới
        SpecField newField = SpecField.builder()
                .group(targetGroup)
                .fieldKey(originalField.getFieldKey())
                .label(originalField.getLabel())
                .dataType(originalField.getDataType())
                .unit(originalField.getUnit())
                .appliesTo(originalField.getAppliesTo())
                .sortOrder(originalField.getSortOrder())
                .isRequired(originalField.getIsRequired())
                .isActive(true)
                .build();

        SpecField savedField = specFieldRepository.save(newField);
        log.info("Spec field copied successfully. New ID: {}", savedField.getId());
        return mapToSpecFieldResponse(savedField);
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Lấy thống kê spec
     */
    @Transactional(readOnly = true)
    public SpecStatisticsDTO getSpecStatistics() {
        long totalGroups = specGroupRepository.countActiveSpecGroups();
        long totalFields = specFieldRepository.countActiveSpecFields();
        long modelFields = specFieldRepository.countActiveSpecFieldsByAppliesTo(SpecField.AppliesTo.MODEL);
        long skuFields = specFieldRepository.countActiveSpecFieldsByAppliesTo(SpecField.AppliesTo.SKU);
        long bothFields = specFieldRepository.countActiveSpecFieldsByAppliesTo(SpecField.AppliesTo.BOTH);

        return SpecStatisticsDTO.builder()
                .totalActiveGroups(totalGroups)
                .totalActiveFields(totalFields)
                .modelOnlyFields(modelFields)
                .skuOnlyFields(skuFields)
                .bothApplicableFields(bothFields)
                .build();
    }

    /**
     * Tìm kiếm spec fields
     */
    @Transactional(readOnly = true)
    public Page<SpecFieldResponse> searchSpecFields(Long groupId, String fieldKey, String label,
                                                   SpecField.DataType dataType, SpecField.AppliesTo appliesTo,
                                                   Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("group.sortOrder", "sortOrder"));
        return specFieldRepository.searchSpecFields(groupId, fieldKey, label, dataType, appliesTo,
                                                   isActive != null ? isActive : true, pageable)
                .map(this::mapToSpecFieldResponse);
    }

    /**
     * Xóa tất cả spec values của ProductModel
     */
    public void deleteAllSpecValuesForProductModel(Long productModelId) {
        log.info("Deleting all spec values for ProductModel ID: {}", productModelId);
        specValueRepository.deleteByProductModelId(productModelId);
        log.info("All spec values deleted for ProductModel ID: {}", productModelId);
    }

    /**
     * Xóa tất cả spec values của SKU
     */
    public void deleteAllSpecValuesForSku(Long skuId) {
        log.info("Deleting all spec values for SKU ID: {}", skuId);
        specValueRepository.deleteBySkuId(skuId);
        log.info("All spec values deleted for SKU ID: {}", skuId);
    }

    // ==================== MAPPER METHODS ====================

    private SpecGroupResponse mapToSpecGroupResponse(SpecGroup specGroup) {
        long fieldCount = specFieldRepository.countActiveSpecFieldsByGroupId(specGroup.getId());

        return SpecGroupResponse.builder()
                .id(specGroup.getId())
                .name(specGroup.getName())
                .sortOrder(specGroup.getSortOrder())
                .isActive(specGroup.getIsActive())
                .createdAt(specGroup.getCreatedAt())
                .fieldCount(fieldCount)
                .build();
    }

    private SpecFieldResponse mapToSpecFieldResponse(SpecField specField) {
        long valueCount = specValueRepository.countByFieldId(specField.getId());

        return SpecFieldResponse.builder()
                .id(specField.getId())
                .groupId(specField.getGroup().getId())
                .groupName(specField.getGroup().getName())
                .fieldKey(specField.getFieldKey())
                .label(specField.getLabel())
                .dataType(specField.getDataType())
                .unit(specField.getUnit())
                .appliesTo(specField.getAppliesTo())
                .sortOrder(specField.getSortOrder())
                .isRequired(specField.getIsRequired())
                .isActive(specField.getIsActive())
                .createdAt(specField.getCreatedAt())
                .valueCount(valueCount)
                .build();
    }

    private SpecValueResponse mapToSpecValueResponse(SpecValue specValue) {
        return SpecValueResponse.builder()
                .id(specValue.getId())
                .fieldId(specValue.getField().getId())
                .fieldKey(specValue.getField().getFieldKey())
                .fieldLabel(specValue.getField().getLabel())
                .dataType(specValue.getField().getDataType())
                .unit(specValue.getField().getUnit())
                .groupId(specValue.getField().getGroup().getId())
                .groupName(specValue.getField().getGroup().getName())
                .productModelId(specValue.getProductModel() != null ? specValue.getProductModel().getId() : null)
                .productModelName(specValue.getProductModel() != null ? specValue.getProductModel().getName() : null)
                .skuId(specValue.getSku() != null ? specValue.getSku().getId() : null)
                .skuCode(specValue.getSku() != null ? specValue.getSku().getCode() : null)
                .value(specValue.getValue())
                .displayValue(formatValueForDisplay(specValue))
                .createdAt(specValue.getCreatedAt())
                .updatedAt(specValue.getUpdatedAt())
                .build();
    }

    private String formatValueForDisplay(SpecValue specValue) {
        Object value = specValue.getValue();
        if (value == null) return null;

        String displayValue = value.toString();
        if (specValue.getField().getUnit() != null && !specValue.getField().getUnit().isEmpty()) {
            displayValue += " " + specValue.getField().getUnit();
        }

        return displayValue;
    }

    /**
     * DTO cho thống kê spec
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SpecStatisticsDTO {
        private Long totalActiveGroups;
        private Long totalActiveFields;
        private Long modelOnlyFields;
        private Long skuOnlyFields;
        private Long bothApplicableFields;
    }
}
