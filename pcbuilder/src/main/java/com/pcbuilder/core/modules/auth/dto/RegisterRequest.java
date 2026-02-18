package com.pcbuilder.core.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username cannot be blank")
    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    @NotBlank(message = "Password cannot be blank")
    @NotNull(message = "Password cannot be null")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_]).*$",
            message = "Password must contain uppercase, lowercase, digit and special character"
    )
    private String password;
    @NotBlank(message = "Confirm Password cannot be blank")
    @NotNull(message = "Confirm Password cannot be null")
    @Size(min = 6, message = "Confirm Password must be at least 6 characters long")
    private String confirmPassword;
    @NotBlank(message = "Email cannot be blank")
    @NotNull(message = "Email cannot be null")
    @Size(max = 50, message = "Email must be less than 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email must be a valid email address")
    private String email;
}
