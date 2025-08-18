package com.example.Backend.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Mật khẩu cũ không được để trống")
    @Size(min = 1, max = 100, message = "Mật khẩu cũ không hợp lệ")
    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu mới phải từ 6-100 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             message = "Mật khẩu mới phải chứa ít nhất 1 chữ thường, 1 chữ hoa và 1 số")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu mới không được để trống")
    private String confirmNewPassword;

    // Validation method để kiểm tra mật khẩu mới khớp
    public boolean isNewPasswordMatching() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }

    // Validation method để kiểm tra mật khẩu cũ và mới khác nhau
    public boolean isPasswordDifferent() {
        return oldPassword != null && newPassword != null && !oldPassword.equals(newPassword);
    }
}
