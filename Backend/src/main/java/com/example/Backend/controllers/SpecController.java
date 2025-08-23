package com.example.Backend.controllers;

import com.example.Backend.dtos.spec.*;
import com.example.Backend.models.SpecField;
import com.example.Backend.services.SpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/specifications")
@RequiredArgsConstructor
@Tag(name = "Specification Management", description = "APIs for managing product specifications")
public class SpecController {

    private final SpecService specService;

    // ==================== SPEC GROUP ENDPOINTS ====================

    @Operation(summary = "Create new spec group", description = "Create a new specification group")
    @PostMapping("/groups")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<SpecGroupResponse> createSpecGroup(
            @Valid @RequestBody SpecGroupRequest request) {
        log.info("Creating new spec group: {}", request.getName());

        SpecGroupResponse response = specService.createSpecGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update spec group", description = "Update existing specification group")
    @PutMapping("/groups/{groupId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<SpecGroupResponse> updateSpecGroup(
            @Parameter(description = "Spec Group ID") @PathVariable Long groupId,
            @Valid @RequestBody SpecGroupRequest request) {
        log.info("Updating spec group with ID: {}", groupId);

        SpecGroupResponse response = specService.updateSpecGroup(groupId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get spec group by ID", description = "Retrieve specification group by ID")
    @GetMapping("/groups/{groupId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<SpecGroupResponse> getSpecGroupById(
            @Parameter(description = "Spec Group ID") @PathVariable Long groupId) {
        log.info("Getting spec group with ID: {}", groupId);

        SpecGroupResponse response = specService.getSpecGroupById(groupId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all active spec groups", description = "Get list of all active specification groups")
    @GetMapping("/groups")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<SpecGroupResponse>> getAllActiveSpecGroups() {
        log.info("Getting all active spec groups");

        List<SpecGroupResponse> response = specService.getAllActiveSpecGroups();
        return ResponseEntity.ok(response);
    }

    // ==================== SPEC FIELD ENDPOINTS ====================

    @Operation(summary = "Create new spec field", description = "Create a new specification field")
    @PostMapping("/fields")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<SpecFieldResponse> createSpecField(
            @Valid @RequestBody SpecFieldRequest request) {
        log.info("Creating new spec field: {} for group: {}", request.getFieldKey(), request.getGroupId());

        SpecFieldResponse response = specService.createSpecField(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update spec field", description = "Update existing specification field")
    @PutMapping("/fields/{fieldId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<SpecFieldResponse> updateSpecField(
            @Parameter(description = "Spec Field ID") @PathVariable Long fieldId,
            @Valid @RequestBody SpecFieldRequest request) {
        log.info("Updating spec field with ID: {}", fieldId);

        SpecFieldResponse response = specService.updateSpecField(fieldId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get spec field by ID", description = "Retrieve specification field by ID")
    @GetMapping("/fields/{fieldId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<SpecFieldResponse> getSpecFieldById(
            @Parameter(description = "Spec Field ID") @PathVariable Long fieldId) {
        log.info("Getting spec field with ID: {}", fieldId);

        SpecFieldResponse response = specService.getSpecFieldById(fieldId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get spec fields by group", description = "Get all specification fields in a group")
    @GetMapping("/groups/{groupId}/fields")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<SpecFieldResponse>> getSpecFieldsByGroup(
            @Parameter(description = "Spec Group ID") @PathVariable Long groupId) {
        log.info("Getting spec fields for group: {}", groupId);

        List<SpecFieldResponse> response = specService.getSpecFieldsByGroup(groupId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search spec fields", description = "Search specification fields with filters")
    @GetMapping("/fields/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<SpecFieldResponse>> searchSpecFields(
            @Parameter(description = "Group ID filter") @RequestParam(required = false) Long groupId,
            @Parameter(description = "Field key filter") @RequestParam(required = false) String fieldKey,
            @Parameter(description = "Label filter") @RequestParam(required = false) String label,
            @Parameter(description = "Data type filter") @RequestParam(required = false) SpecField.DataType dataType,
            @Parameter(description = "Applies to filter") @RequestParam(required = false) SpecField.AppliesTo appliesTo,
            @Parameter(description = "Active status filter") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.info("Searching spec fields with filters");

        Page<SpecFieldResponse> response = specService.searchSpecFields(
                groupId, fieldKey, label, dataType, appliesTo, isActive, page, size);
        return ResponseEntity.ok(response);
    }

    // ==================== SPEC VALUE ENDPOINTS ====================

    @Operation(summary = "Save spec values for ProductModel", description = "Save/update specification values for a product model")
    @PostMapping("/values/product-models/{productModelId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER') or hasRole('STAFF')")
    public ResponseEntity<List<SpecValueResponse>> saveSpecValuesForProductModel(
            @Parameter(description = "Product Model ID") @PathVariable Long productModelId,
            @RequestBody Map<Long, Object> fieldValues) {
        log.info("Saving spec values for ProductModel ID: {}", productModelId);

        List<SpecValueResponse> response = specService.saveSpecValuesForProductModel(productModelId, fieldValues);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Save spec values for SKU", description = "Save/update specification values for a SKU")
    @PostMapping("/values/skus/{skuId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER') or hasRole('STAFF')")
    public ResponseEntity<List<SpecValueResponse>> saveSpecValuesForSku(
            @Parameter(description = "SKU ID") @PathVariable Long skuId,
            @RequestBody Map<Long, Object> fieldValues) {
        log.info("Saving spec values for SKU ID: {}", skuId);

        List<SpecValueResponse> response = specService.saveSpecValuesForSku(skuId, fieldValues);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get spec values for ProductModel", description = "Get all specification values for a product model")
    @GetMapping("/values/product-models/{productModelId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<SpecValueResponse>> getSpecValuesForProductModel(
            @Parameter(description = "Product Model ID") @PathVariable Long productModelId) {
        log.info("Getting spec values for ProductModel ID: {}", productModelId);

        List<SpecValueResponse> response = specService.getSpecValuesForProductModel(productModelId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get spec values for SKU", description = "Get all specification values for a SKU")
    @GetMapping("/values/skus/{skuId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<SpecValueResponse>> getSpecValuesForSku(
            @Parameter(description = "SKU ID") @PathVariable Long skuId) {
        log.info("Getting spec values for SKU ID: {}", skuId);

        List<SpecValueResponse> response = specService.getSpecValuesForSku(skuId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete spec value", description = "Delete a specification value")
    @DeleteMapping("/values/{specValueId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<Void> deleteSpecValue(
            @Parameter(description = "Spec Value ID") @PathVariable Long specValueId) {
        log.info("Deleting spec value with ID: {}", specValueId);

        specService.deleteSpecValue(specValueId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all spec values for ProductModel", description = "Delete all specification values for a product model")
    @DeleteMapping("/values/product-models/{productModelId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<Void> deleteAllSpecValuesForProductModel(
            @Parameter(description = "Product Model ID") @PathVariable Long productModelId) {
        log.info("Deleting all spec values for ProductModel ID: {}", productModelId);

        specService.deleteAllSpecValuesForProductModel(productModelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all spec values for SKU", description = "Delete all specification values for a SKU")
    @DeleteMapping("/values/skus/{skuId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<Void> deleteAllSpecValuesForSku(
            @Parameter(description = "SKU ID") @PathVariable Long skuId) {
        log.info("Deleting all spec values for SKU ID: {}", skuId);

        specService.deleteAllSpecValuesForSku(skuId);
        return ResponseEntity.noContent().build();
    }

    // ==================== STATISTICS ENDPOINTS ====================

    @Operation(summary = "Get spec statistics", description = "Get specification system statistics")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SpecService.SpecStatisticsDTO> getSpecStatistics() {
        log.info("Getting spec statistics");

        SpecService.SpecStatisticsDTO statistics = specService.getSpecStatistics();
        return ResponseEntity.ok(statistics);
    }

    // ==================== DELETE, ACTIVATE, DEACTIVATE APIs FOR SPEC GROUPS ====================

    @Operation(summary = "Activate spec group", description = "Activate a deactivated specification group")
    @PutMapping("/groups/{groupId}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<Void> activateSpecGroup(
            @Parameter(description = "Spec Group ID") @PathVariable Long groupId) {
        log.info("Activating spec group with ID: {}", groupId);

        specService.activateSpecGroup(groupId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Deactivate spec group", description = "Deactivate specification group (soft delete)")
    @PutMapping("/groups/{groupId}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<Void> deactivateSpecGroup(
            @Parameter(description = "Spec Group ID") @PathVariable Long groupId) {
        log.info("Deactivating spec group with ID: {}", groupId);

        specService.deactivateSpecGroup(groupId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get inactive spec groups", description = "Get list of all deactivated specification groups")
    @GetMapping("/groups/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SpecGroupResponse>> getInactiveSpecGroups() {
        log.info("Getting all inactive spec groups");

        List<SpecGroupResponse> response = specService.getInactiveSpecGroups();
        return ResponseEntity.ok(response);
    }

    // ==================== DELETE, ACTIVATE, DEACTIVATE APIs FOR SPEC FIELDS ====================

    @Operation(summary = "Activate spec field", description = "Activate a deactivated specification field")
    @PutMapping("/fields/{fieldId}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<Void> activateSpecField(
            @Parameter(description = "Spec Field ID") @PathVariable Long fieldId) {
        log.info("Activating spec field with ID: {}", fieldId);

        specService.activateSpecField(fieldId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Deactivate spec field", description = "Deactivate specification field (soft delete)")
    @PutMapping("/fields/{fieldId}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<Void> deactivateSpecField(
            @Parameter(description = "Spec Field ID") @PathVariable Long fieldId) {
        log.info("Deactivating spec field with ID: {}", fieldId);

        specService.deactivateSpecField(fieldId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get inactive spec fields", description = "Get list of all deactivated specification fields")
    @GetMapping("/fields/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SpecFieldResponse>> getInactiveSpecFields(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.info("Getting all inactive spec fields");

        Page<SpecFieldResponse> response = specService.getInactiveSpecFields(page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get spec fields by applies to", description = "Get specification fields filtered by applies to (MODEL, SKU, BOTH)")
    @GetMapping("/fields/applies-to/{appliesTo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<SpecFieldResponse>> getSpecFieldsByAppliesTo(
            @Parameter(description = "Applies to filter") @PathVariable SpecField.AppliesTo appliesTo) {
        log.info("Getting spec fields by applies to: {}", appliesTo);

        List<SpecFieldResponse> response = specService.getSpecFieldsByAppliesTo(appliesTo);
        return ResponseEntity.ok(response);
    }

    // ==================== BULK OPERATIONS ====================

    @Operation(summary = "Bulk activate spec groups", description = "Activate multiple specification groups at once")
    @PutMapping("/groups/bulk/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> bulkActivateSpecGroups(
            @Parameter(description = "List of spec group IDs") @RequestBody List<Long> groupIds) {
        log.info("Bulk activating spec groups: {}", groupIds);

        specService.bulkActivateSpecGroups(groupIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Bulk deactivate spec groups", description = "Deactivate multiple specification groups at once")
    @PutMapping("/groups/bulk/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> bulkDeactivateSpecGroups(
            @Parameter(description = "List of spec group IDs") @RequestBody List<Long> groupIds) {
        log.info("Bulk deactivating spec groups: {}", groupIds);

        specService.bulkDeactivateSpecGroups(groupIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Bulk activate spec fields", description = "Activate multiple specification fields at once")
    @PutMapping("/fields/bulk/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> bulkActivateSpecFields(
            @Parameter(description = "List of spec field IDs") @RequestBody List<Long> fieldIds) {
        log.info("Bulk activating spec fields: {}", fieldIds);

        specService.bulkActivateSpecFields(fieldIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Bulk deactivate spec fields", description = "Deactivate multiple specification fields at once")
    @PutMapping("/fields/bulk/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> bulkDeactivateSpecFields(
            @Parameter(description = "List of spec field IDs") @RequestBody List<Long> fieldIds) {
        log.info("Bulk deactivating spec fields: {}", fieldIds);

        specService.bulkDeactivateSpecFields(fieldIds);
        return ResponseEntity.ok().build();
    }

    // ==================== COPY/CLONE OPERATIONS ====================

    @Operation(summary = "Clone spec group", description = "Create a copy of existing specification group with all its fields")
    @PostMapping("/groups/{groupId}/clone")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<SpecGroupResponse> cloneSpecGroup(
            @Parameter(description = "Source spec group ID") @PathVariable Long groupId,
            @Parameter(description = "New group name") @RequestParam String newName) {
        log.info("Cloning spec group ID: {} with new name: {}", groupId, newName);

        SpecGroupResponse response = specService.cloneSpecGroup(groupId, newName);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Copy spec field to another group", description = "Copy specification field to another group")
    @PostMapping("/fields/{fieldId}/copy")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<SpecFieldResponse> copySpecFieldToGroup(
            @Parameter(description = "Source field ID") @PathVariable Long fieldId,
            @Parameter(description = "Target group ID") @RequestParam Long targetGroupId) {
        log.info("Copying spec field ID: {} to group ID: {}", fieldId, targetGroupId);

        SpecFieldResponse response = specService.copySpecFieldToGroup(fieldId, targetGroupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
