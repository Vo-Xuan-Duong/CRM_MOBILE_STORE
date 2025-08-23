package com.example.Backend.dtos.customer;

import com.example.Backend.models.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateDTO {

    private String fullName;

    @Pattern(regexp = "^(\\+84|0)[3-9][0-9]{8}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Email(message = "Email không hợp lệ")
    private String email;

    private LocalDate birthDate;
    private Customer.Gender gender;
    // Address fields
    private String address;

    private String note;
}
