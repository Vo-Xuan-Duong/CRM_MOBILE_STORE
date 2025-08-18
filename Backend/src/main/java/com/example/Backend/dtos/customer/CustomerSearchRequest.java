package com.example.Backend.dtos.customer;

import com.example.Backend.models.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchRequest {

    private String fullName;
    private String phone;
    private String email;
    private Customer.Gender gender;
    private String city;
    private String province;
    private String ward;
    private String district;

    // Tìm kiếm theo ngày sinh
    private LocalDate birthDateFrom;
    private LocalDate birthDateTo;

    // Tìm kiếm theo ngày tạo
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;

    // Tìm kiếm theo thống kê
    private Integer minOrders;
    private Integer maxOrders;
    private Double minSpent;
    private Double maxSpent;

    // Pagination
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}
