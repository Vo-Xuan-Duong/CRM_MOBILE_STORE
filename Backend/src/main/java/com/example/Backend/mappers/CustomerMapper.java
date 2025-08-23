package com.example.Backend.mappers;

import com.example.Backend.dtos.customer.CustomerCreateDTO;
import com.example.Backend.dtos.customer.CustomerResponseDTO;
import com.example.Backend.dtos.customer.CustomerUpdateDTO;
import com.example.Backend.models.Customer;
import com.example.Backend.repositorys.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

    private final SalesOrderRepository salesOrderRepository;

    public CustomerResponseDTO toResponseDTO(Customer customer) {
        if (customer == null) return null;

        // Calculate customer order statistics with null safety
        Long totalOrders = salesOrderRepository.countCompletedOrdersByCustomer(customer.getId());
        BigDecimal totalSpent = salesOrderRepository.getTotalSpentByCustomer(customer.getId());
        var lastOrderDate = salesOrderRepository.getLastOrderDateByCustomer(customer.getId());

        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .birthDate(customer.getBirthDate())
                .gender(customer.getGender())
                .tier(customer.getTier())
                .fullAddress(buildFullAddress(customer))
                .note(customer.getNotes())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                // Order statistics with proper null handling
                .totalOrders(totalOrders != null ? totalOrders.intValue() : 0)
                .totalSpent(totalSpent != null ? totalSpent.doubleValue() : 0.0)
                .lastOrderDate(lastOrderDate.orElse(null))
                .build();
    }

    public Customer toEntity(CustomerCreateDTO createDTO) {
        if (createDTO == null) return null;

        return Customer.builder()
                .fullName(createDTO.getFullName())
                .phone(createDTO.getPhone())
                .email(createDTO.getEmail())
                .birthDate(createDTO.getBirthDate())
                .gender(createDTO.getGender())
                .address(sanitizeAddress(createDTO.getAddress()))
                .notes(createDTO.getNote())
                .build();
    }

    public void updateEntityFromDTO(Customer customer, CustomerUpdateDTO updateDTO) {
        if (customer == null || updateDTO == null) return;

        if (updateDTO.getFullName() != null) {
            customer.setFullName(updateDTO.getFullName());
        }
        if (updateDTO.getPhone() != null) {
            customer.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getEmail() != null) {
            customer.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getBirthDate() != null) {
            customer.setBirthDate(updateDTO.getBirthDate());
        }
        if (updateDTO.getGender() != null) {
            customer.setGender(updateDTO.getGender());
        }

        // Update address if provided
        if (updateDTO.getAddress() != null) {
            customer.setAddress(sanitizeAddress(updateDTO.getAddress()));
        }

        if (updateDTO.getNote() != null) {
            customer.setNotes(updateDTO.getNote());
        }
    }

    /**
     * Builds the full address for display purposes
     * Returns the customer's address or empty string if null/empty
     */
    private String buildFullAddress(Customer customer) {
        return customer.getAddress() != null && !customer.getAddress().trim().isEmpty()
            ? customer.getAddress().trim()
            : "";
    }

    /**
     * Sanitizes address input by trimming whitespace and handling null values
     */
    private String sanitizeAddress(String address) {
        return address != null ? address.trim() : null;
    }
}
