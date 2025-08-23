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
public class CustomerResponseDTO {

    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private LocalDate birthDate;
    private Customer.Gender gender;
    private Customer.CustomerTier tier;
    private String fullAddress;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Thống kê khách hàng
    private Integer totalOrders;
    private Double totalSpent;
    private LocalDateTime lastOrderDate;
}
