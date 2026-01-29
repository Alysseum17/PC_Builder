package com.pcbuilder.core.modules.auth.dto;

import com.pcbuilder.core.modules.auth.dto.validation.Login;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Login cannot be blank")
    @NotNull(message = "Login cannot be null")
    @Login(message = "Login must be a valid email or username (3-50 chars)")
    private String login;


    @NotBlank(message = "Password cannot be blank")
    @NotNull(message = "Password cannot be null")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_]).*$",
            message = "Password must contain uppercase, lowercase, digit and special character"
    )
    private String password;


}
