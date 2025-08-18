package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.spec.SpecGroupRequest;
import com.example.Backend.dtos.spec.SpecGroupResponse;
import com.example.Backend.dtos.spec.SpecFieldRequest;
import com.example.Backend.dtos.spec.SpecFieldResponse;
import com.example.Backend.dtos.spec.SpecValueRequest;
import com.example.Backend.dtos.spec.SpecValueResponse;
import com.example.Backend.services.SpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specifications")
@RequiredArgsConstructor
@Tag(name = "Specification", description = "Product Specification Management API")
public class SpecController {

    private final SpecService specService;

    // Spec Group endpoints
    @PostMapping("/groups")
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @Operation(summary = "Create new spec group")
    public ResponseEntity<ResponseData<SpecGroupResponse>> createSpecGroup(
            @Valid @RequestBody SpecGroupRequest request) {
        SpecGroupResponse response = specService.createSpecGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<SpecGroupResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Spec group created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/groups")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get all spec groups")
    public ResponseEntity<ResponseData<List<SpecGroupResponse>>> getAllSpecGroups() {
        List<SpecGroupResponse> response = specService.getAllSpecGroups();
        return ResponseEntity.ok(ResponseData.<List<SpecGroupResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Spec groups retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/groups/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get spec group by ID")
    public ResponseEntity<ResponseData<SpecGroupResponse>> getSpecGroupById(@PathVariable Long id) {
        SpecGroupResponse response = specService.getSpecGroupById(id);
        return ResponseEntity.ok(ResponseData.<SpecGroupResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Spec group retrieved successfully")
                .data(response)
                .build());
    }

    // Spec Field endpoints
    @PostMapping("/fields")
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @Operation(summary = "Create new spec field")
    public ResponseEntity<ResponseData<SpecFieldResponse>> createSpecField(
            @Valid @RequestBody SpecFieldRequest request) {
        SpecFieldResponse response = specService.createSpecField(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<SpecFieldResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Spec field created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/fields")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get all spec fields")
    public ResponseEntity<ResponseData<Page<SpecFieldResponse>>> getAllSpecFields(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SpecFieldResponse> response = specService.getAllSpecFields(pageable);
        return ResponseEntity.ok(ResponseData.<Page<SpecFieldResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Spec fields retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/fields/group/{groupId}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get spec fields by group")
    public ResponseEntity<ResponseData<List<SpecFieldResponse>>> getSpecFieldsByGroup(@PathVariable Long groupId) {
        List<SpecFieldResponse> response = specService.getSpecFieldsByGroup(groupId);
        return ResponseEntity.ok(ResponseData.<List<SpecFieldResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Spec fields by group retrieved successfully")
                .data(response)
                .build());
    }

    // Spec Value endpoints
    @PostMapping("/values")
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @Operation(summary = "Create new spec value")
    public ResponseEntity<ResponseData<SpecValueResponse>> createSpecValue(
            @Valid @RequestBody SpecValueRequest request) {
        SpecValueResponse response = specService.createSpecValue(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<SpecValueResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Spec value created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/values/model/{modelId}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get spec values by product model")
    public ResponseEntity<ResponseData<List<SpecValueResponse>>> getSpecValuesByModel(@PathVariable Long modelId) {
        List<SpecValueResponse> response = specService.getSpecValuesByModel(modelId);
        return ResponseEntity.ok(ResponseData.<List<SpecValueResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Spec values by model retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/values/sku/{skuId}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get spec values by SKU")
    public ResponseEntity<ResponseData<List<SpecValueResponse>>> getSpecValuesBySku(@PathVariable Long skuId) {
        List<SpecValueResponse> response = specService.getSpecValuesBySku(skuId);
        return ResponseEntity.ok(ResponseData.<List<SpecValueResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Spec values by SKU retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/values/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @Operation(summary = "Update spec value")
    public ResponseEntity<ResponseData<SpecValueResponse>> updateSpecValue(
            @PathVariable Long id,
            @Valid @RequestBody SpecValueRequest request) {
        SpecValueResponse response = specService.updateSpecValue(id, request);
        return ResponseEntity.ok(ResponseData.<SpecValueResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Spec value updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/values/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    @Operation(summary = "Delete spec value")
    public ResponseEntity<ResponseData<Void>> deleteSpecValue(@PathVariable Long id) {
        specService.deleteSpecValue(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Spec value deleted successfully")
                .build());
    }
}
