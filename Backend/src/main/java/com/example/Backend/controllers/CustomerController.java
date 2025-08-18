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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "Get all customers with pagination")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<Page<CustomerResponseDTO>>> getAllCustomers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CustomerResponseDTO> customers = customerService.getCustomersPaginated(pageable);

        return ResponseEntity.ok(ResponseData.<Page<CustomerResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách khách hàng thành công")
                .data(customers)
                .build());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all customers without pagination")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<List<CustomerResponseDTO>>> getAllCustomersNoPagination() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();

        return ResponseEntity.ok(ResponseData.<List<CustomerResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy tất cả khách hàng thành công")
                .data(customers)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<CustomerResponseDTO>> getCustomerById(
            @Parameter(description = "Customer ID") @PathVariable Long id) {

        CustomerResponseDTO customer = customerService.getCustomerById(id);

        return ResponseEntity.ok(ResponseData.<CustomerResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin khách hàng thành công")
                .data(customer)
                .build());
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "Get customer by phone number")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<CustomerResponseDTO>> getCustomerByPhone(
            @Parameter(description = "Phone number") @PathVariable String phone) {

        CustomerResponseDTO customer = customerService.getCustomerByPhone(phone);

        return ResponseEntity.ok(ResponseData.<CustomerResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Tìm khách hàng theo số điện thoại thành công")
                .data(customer)
                .build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get customer by email")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<CustomerResponseDTO>> getCustomerByEmail(
            @Parameter(description = "Email address") @PathVariable String email) {

        CustomerResponseDTO customer = customerService.getCustomerByEmail(email);

        return ResponseEntity.ok(ResponseData.<CustomerResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Tìm khách hàng theo email thành công")
                .data(customer)
                .build());
    }

    @PostMapping("/search")
    @Operation(summary = "Search customers with advanced criteria")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<Page<CustomerResponseDTO>>> searchCustomers(
            @Valid @RequestBody CustomerSearchRequest searchRequest) {

        Page<CustomerResponseDTO> customers = customerService.searchCustomers(searchRequest);

        return ResponseEntity.ok(ResponseData.<Page<CustomerResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Tìm kiếm khách hàng thành công")
                .data(customers)
                .build());
    }

    @GetMapping("/search/name")
    @Operation(summary = "Search customers by name")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<List<CustomerResponseDTO>>> searchCustomersByName(
            @Parameter(description = "Customer name") @RequestParam String name) {

        List<CustomerResponseDTO> customers = customerService.searchCustomersByName(name);

        return ResponseEntity.ok(ResponseData.<List<CustomerResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Tìm kiếm khách hàng theo tên thành công")
                .data(customers)
                .build());
    }

    @GetMapping("/gender/{gender}")
    @Operation(summary = "Get customers by gender")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<List<CustomerResponseDTO>>> getCustomersByGender(
            @Parameter(description = "Gender") @PathVariable Customer.Gender gender) {

        List<CustomerResponseDTO> customers = customerService.getCustomersByGender(gender);

        return ResponseEntity.ok(ResponseData.<List<CustomerResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy khách hàng theo giới tính thành công")
                .data(customers)
                .build());
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get customers by city")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<List<CustomerResponseDTO>>> getCustomersByCity(
            @Parameter(description = "City name") @PathVariable String city) {

        List<CustomerResponseDTO> customers = customerService.getCustomersByCity(city);

        return ResponseEntity.ok(ResponseData.<List<CustomerResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy khách hàng theo thành phố thành công")
                .data(customers)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create new customer")
    @PreAuthorize("hasAuthority('CUSTOMER_CREATE')")
    public ResponseEntity<ResponseData<CustomerResponseDTO>> createCustomer(
            @Valid @RequestBody CustomerCreateDTO createDTO) {

        CustomerResponseDTO customer = customerService.createCustomer(createDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<CustomerResponseDTO>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Tạo khách hàng mới thành công")
                        .data(customer)
                        .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE')")
    public ResponseEntity<ResponseData<CustomerResponseDTO>> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateDTO updateDTO) {

        CustomerResponseDTO customer = customerService.updateCustomer(id, updateDTO);

        return ResponseEntity.ok(ResponseData.<CustomerResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật thông tin khách hàng thành công")
                .data(customer)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer")
    @PreAuthorize("hasAuthority('CUSTOMER_DELETE')")
    public ResponseEntity<ResponseData<Void>> deleteCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Xóa khách hàng thành công")
                .build());
    }

    @GetMapping("/exists/phone/{phone}")
    @Operation(summary = "Check if phone exists")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<Boolean>> checkPhoneExists(
            @Parameter(description = "Phone number") @PathVariable String phone) {

        boolean exists = customerService.existsByPhone(phone);

        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message("Kiểm tra số điện thoại hoàn tất")
                .data(exists)
                .build());
    }

    @GetMapping("/exists/email/{email}")
    @Operation(summary = "Check if email exists")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<Boolean>> checkEmailExists(
            @Parameter(description = "Email address") @PathVariable String email) {

        boolean exists = customerService.existsByEmail(email);

        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message("Kiểm tra email hoàn tất")
                .data(exists)
                .build());
    }

    @GetMapping("/count")
    @Operation(summary = "Get total customer count")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<Long>> getCustomerCount() {
        long count = customerService.countCustomers();

        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy tổng số khách hàng thành công")
                .data(count)
                .build());
    }

    @GetMapping("/stats/gender")
    @Operation(summary = "Get customer statistics by gender")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<Map<String, Long>>> getCustomerStatsByGender() {
        Map<String, Long> stats = customerService.getCustomerStatsByGender();

        return ResponseEntity.ok(ResponseData.<Map<String, Long>>builder()
                .status(HttpStatus.OK.value())
                .message("Thống kê khách hàng theo giới tính thành công")
                .data(stats)
                .build());
    }

    @GetMapping("/stats/province")
    @Operation(summary = "Get customer statistics by province")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<Map<String, Long>>> getCustomerStatsByProvince() {
        Map<String, Long> stats = customerService.getCustomerStatsByProvince();

        return ResponseEntity.ok(ResponseData.<Map<String, Long>>builder()
                .status(HttpStatus.OK.value())
                .message("Thống kê khách hàng theo tỉnh thành công")
                .data(stats)
                .build());
    }

    @GetMapping("/stats/month/{year}")
    @Operation(summary = "Get customer statistics by month")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<ResponseData<Map<Integer, Long>>> getCustomerStatsByMonth(
            @Parameter(description = "Year") @PathVariable int year) {

        Map<Integer, Long> stats = customerService.getCustomerStatsByMonth(year);

        return ResponseEntity.ok(ResponseData.<Map<Integer, Long>>builder()
                .status(HttpStatus.OK.value())
                .message("Thống kê khách hàng theo tháng thành công")
                .data(stats)
                .build());
    }
}
