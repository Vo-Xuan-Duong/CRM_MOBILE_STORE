package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.customer.CustomerCreateDTO;
import com.example.Backend.dtos.customer.CustomerResponseDTO;
import com.example.Backend.dtos.customer.CustomerSearchRequest;
import com.example.Backend.dtos.customer.CustomerUpdateDTO;
import com.example.Backend.models.Customer;
import com.example.Backend.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Create new customer", description = "Create a new customer in the system")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<CustomerResponseDTO>> createCustomer(
            @Valid @RequestBody CustomerCreateDTO createDTO) {
        try {
            log.info("Creating new customer: {}", createDTO.getFullName());
            CustomerResponseDTO response = customerService.createCustomer(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<CustomerResponseDTO>builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Customer created successfully")
                            .data(response)
                            .build());
        } catch (Exception e) {
            log.error("Error creating customer: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<CustomerResponseDTO>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error creating customer: " + e.getMessage())
                            .build());
        }
    }

    @Operation(summary = "Update customer", description = "Update existing customer information")
    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<CustomerResponseDTO>> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            @Valid @RequestBody CustomerUpdateDTO updateDTO) {
        try {
            log.info("Updating customer with ID: {}", customerId);
            CustomerResponseDTO response = customerService.updateCustomer(customerId, updateDTO);
            return ResponseEntity.ok(ResponseData.<CustomerResponseDTO>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customer updated successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error updating customer ID {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<CustomerResponseDTO>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error updating customer: " + e.getMessage())
                            .build());
        }
    }

    @Operation(summary = "Get customer by ID", description = "Retrieve customer information by ID")
    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<CustomerResponseDTO>> getCustomerById(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        try {
            log.info("Getting customer with ID: {}", customerId);
            CustomerResponseDTO response = customerService.getCustomerById(customerId);
            return ResponseEntity.ok(ResponseData.<CustomerResponseDTO>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customer retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting customer ID {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<CustomerResponseDTO>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error retrieving customer: " + e.getMessage())
                            .build());
        }
    }

    @Operation(summary = "Get customer by phone", description = "Find customer by phone number")
    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<CustomerResponseDTO>> getCustomerByPhone(
            @Parameter(description = "Phone number") @PathVariable String phone) {
        try {
            log.info("Getting customer with phone: {}", phone);
            CustomerResponseDTO response = customerService.getCustomerByPhone(phone);
            return ResponseEntity.ok(ResponseData.<CustomerResponseDTO>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customer retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting customer with phone {}: {}", phone, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<CustomerResponseDTO>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error retrieving customer: " + e.getMessage())
                            .build());
        }
    }

    @Operation(summary = "Search customers", description = "Search customers with filters and pagination")
    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<Page<CustomerResponseDTO>>> searchCustomers(
            @RequestBody CustomerSearchRequest searchRequest) {
        try {
            log.info("Searching customers with criteria: {}", searchRequest);
            Page<CustomerResponseDTO> response = customerService.searchCustomers(searchRequest);
            return ResponseEntity.ok(ResponseData.<Page<CustomerResponseDTO>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customers searched successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error searching customers: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<CustomerResponseDTO>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error searching customers: " + e.getMessage())
                            .build());
        }
    }

    @Operation(summary = "Get all active customers", description = "Get paginated list of all active customers")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<Page<CustomerResponseDTO>>> getAllActiveCustomers(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        try {
            log.info("Getting all active customers - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
            Page<CustomerResponseDTO> response = customerService.getAllActiveCustomers(pageable);
            return ResponseEntity.ok(ResponseData.<Page<CustomerResponseDTO>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Active customers retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting active customers: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<CustomerResponseDTO>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error retrieving active customers: " + e.getMessage())
                            .build());
        }
    }

    @Operation(summary = "Deactivate customer", description = "Soft delete customer (set inactive)")
    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deactivateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        try {
            log.info("Deactivating customer with ID: {}", customerId);
            customerService.deactivateCustomer(customerId);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customer deactivated successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error deactivating customer ID {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error deactivating customer: " + e.getMessage())
                            .build());
        }
    }

    @Operation(summary = "Activate customer", description = "Reactivate deactivated customer")
    @PatchMapping("/{customerId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> activateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        try {
            log.info("Activating customer with ID: {}", customerId);
            customerService.activateCustomer(customerId);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customer activated successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error activating customer ID {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error activating customer: " + e.getMessage())
                            .build());
        }
    }

    @Operation(summary = "Upgrade customer tier", description = "Upgrade customer to higher tier")
    @PatchMapping("/{customerId}/tier")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ResponseData<CustomerResponseDTO>> upgradeTier(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            @Parameter(description = "New tier") @RequestParam Customer.CustomerTier tier) {
        try {
            log.info("Upgrading customer tier - ID: {}, Tier: {}", customerId, tier);
            CustomerResponseDTO response = customerService.upgradeTier(customerId, tier);
            return ResponseEntity.ok(ResponseData.<CustomerResponseDTO>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customer tier upgraded successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error upgrading customer tier ID {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<CustomerResponseDTO>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error upgrading customer tier: " + e.getMessage())
                            .build());
        }
    }

    @Operation(summary = "Get customer statistics", description = "Get customer statistics and analytics")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ResponseData<CustomerService.CustomerStatisticsDTO>> getCustomerStatistics() {
        try {
            log.info("Getting customer statistics");
            CustomerService.CustomerStatisticsDTO statistics = customerService.getCustomerStatistics();
            return ResponseEntity.ok(ResponseData.<CustomerService.CustomerStatisticsDTO>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customer statistics retrieved successfully")
                    .data(statistics)
                    .build());
        } catch (Exception e) {
            log.error("Error getting customer statistics: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<CustomerService.CustomerStatisticsDTO>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error retrieving customer statistics: " + e.getMessage())
                            .build());
        }
    }

    @Operation(summary = "Quick search", description = "Quick search customers by name or phone")
    @GetMapping("/quick-search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<Page<CustomerResponseDTO>>> quickSearch(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Quick search with keyword: {}", keyword);
            CustomerSearchRequest searchRequest = CustomerSearchRequest.builder()
                    .fullName(keyword)
                    .phone(keyword)
                    .page(page)
                    .size(size)
                    .sortBy("createdAt")
                    .sortDirection("DESC")
                    .build();

            Page<CustomerResponseDTO> response = customerService.searchCustomers(searchRequest);
            return ResponseEntity.ok(ResponseData.<Page<CustomerResponseDTO>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Quick search completed successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error in quick search with keyword {}: {}", keyword, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<CustomerResponseDTO>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error in quick search: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{customerId}/hard-delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Hard delete customer", description = "Permanently delete customer from the system")
    public ResponseEntity<ResponseData<Void>> hardDeleteCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        try {
            log.info("Hard deleting customer with ID: {}", customerId);
            customerService.hardDeleteCustomer(customerId);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customer permanently deleted successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error hard deleting customer ID {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error permanently deleting customer: " + e.getMessage())
                            .build());
        }
    }
}
