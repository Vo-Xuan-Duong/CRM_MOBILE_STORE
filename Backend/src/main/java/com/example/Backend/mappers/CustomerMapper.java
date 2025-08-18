package com.example.Backend.mappers;

import com.example.Backend.dtos.customer.CustomerCreateDTO;
import com.example.Backend.dtos.customer.CustomerResponseDTO;
import com.example.Backend.dtos.customer.CustomerUpdateDTO;
import com.example.Backend.models.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponseDTO toResponseDTO(Customer customer) {
        if (customer == null) return null;

        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .birthDate(customer.getBirthDate())
                .gender(customer.getGender())
                .addressLine(customer.getAddressLine())
                .ward(customer.getWard())
                .district(customer.getDistrict())
                .city(customer.getCity())
                .province(customer.getProvince())
                .fullAddress(buildFullAddress(customer))
                .note(customer.getNote())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                // TODO: Thêm thống kê đơn hàng từ OrderService khi có
                .totalOrders(0)
                .totalSpent(0.0)
                .lastOrderDate(null)
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
                .addressLine(createDTO.getAddressLine())
                .ward(createDTO.getWard())
                .district(createDTO.getDistrict())
                .city(createDTO.getCity())
                .province(createDTO.getProvince())
                .note(createDTO.getNote())
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
        if (updateDTO.getAddressLine() != null) {
            customer.setAddressLine(updateDTO.getAddressLine());
        }
        if (updateDTO.getWard() != null) {
            customer.setWard(updateDTO.getWard());
        }
        if (updateDTO.getDistrict() != null) {
            customer.setDistrict(updateDTO.getDistrict());
        }
        if (updateDTO.getCity() != null) {
            customer.setCity(updateDTO.getCity());
        }
        if (updateDTO.getProvince() != null) {
            customer.setProvince(updateDTO.getProvince());
        }
        if (updateDTO.getNote() != null) {
            customer.setNote(updateDTO.getNote());
        }
    }

    private String buildFullAddress(Customer customer) {
        StringBuilder address = new StringBuilder();

        if (customer.getAddressLine() != null && !customer.getAddressLine().trim().isEmpty()) {
            address.append(customer.getAddressLine());
        }

        if (customer.getWard() != null && !customer.getWard().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(customer.getWard());
        }

        if (customer.getDistrict() != null && !customer.getDistrict().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(customer.getDistrict());
        }

        if (customer.getCity() != null && !customer.getCity().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(customer.getCity());
        }

        if (customer.getProvince() != null && !customer.getProvince().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(customer.getProvince());
        }

        return address.toString();
    }
}
