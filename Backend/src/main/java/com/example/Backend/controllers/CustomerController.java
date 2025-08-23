package com.example.Backend.controllers;

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
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Valid @RequestBody CustomerCreateDTO createDTO) {
        log.info("Creating new customer: {}", createDTO.getFullName());

        CustomerResponseDTO response = customerService.createCustomer(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update customer", description = "Update existing customer information")
    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            @Valid @RequestBody CustomerUpdateDTO updateDTO) {
        log.info("Updating customer with ID: {}", customerId);

        CustomerResponseDTO response = customerService.updateCustomer(customerId, updateDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get customer by ID", description = "Retrieve customer information by ID")
    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        log.info("Getting customer with ID: {}", customerId);

        CustomerResponseDTO response = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get customer by phone", description = "Find customer by phone number")
    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CustomerResponseDTO> getCustomerByPhone(
            @Parameter(description = "Phone number") @PathVariable String phone) {
        log.info("Getting customer with phone: {}", phone);

        CustomerResponseDTO response = customerService.getCustomerByPhone(phone);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search customers", description = "Search customers with filters and pagination")
    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<CustomerResponseDTO>> searchCustomers(
            @RequestBody CustomerSearchRequest searchRequest) {
        log.info("Searching customers with criteria: {}", searchRequest);

        Page<CustomerResponseDTO> response = customerService.searchCustomers(searchRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all active customers", description = "Get paginated list of all active customers")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<CustomerResponseDTO>> getAllActiveCustomers(@PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable) {
        log.info("Getting all active customers - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<CustomerResponseDTO> response = customerService.getAllActiveCustomers(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deactivate customer", description = "Soft delete customer (set inactive)")
    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        log.info("Deactivating customer with ID: {}", customerId);

        customerService.deactivateCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activate customer", description = "Reactivate deactivated customer")
    @PatchMapping("/{customerId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        log.info("Activating customer with ID: {}", customerId);

        customerService.activateCustomer(customerId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Upgrade customer tier", description = "Upgrade customer to higher tier")
    @PatchMapping("/{customerId}/tier")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<CustomerResponseDTO> upgradeTier(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            @Parameter(description = "New tier") @RequestParam Customer.CustomerTier tier) {
        log.info("Upgrading customer tier - ID: {}, Tier: {}", customerId, tier);

        CustomerResponseDTO response = customerService.upgradeTier(customerId, tier);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get customer statistics", description = "Get customer statistics and analytics")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<CustomerService.CustomerStatisticsDTO> getCustomerStatistics() {
        log.info("Getting customer statistics");

        CustomerService.CustomerStatisticsDTO statistics = customerService.getCustomerStatistics();
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Quick search", description = "Quick search customers by name or phone")
    @GetMapping("/quick-search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<CustomerResponseDTO>> quickSearch(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
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
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}/hard-delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Hard delete customer", description = "Permanently delete customer from the system")
    public ResponseEntity<Void> hardDeleteCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        log.info("Hard deleting customer with ID: {}", customerId);
        customerService.hardDeleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}
