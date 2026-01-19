package com.pcbuilder.core.modules.auth.controller;

import com.pcbuilder.core.modules.auth.dto.RegisterDto;
import com.pcbuilder.core.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public String register(@RequestBody RegisterDto registerDto) throws Exception {
        authService.registerUser(registerDto);
        return "User registered successfully";
    }
}
