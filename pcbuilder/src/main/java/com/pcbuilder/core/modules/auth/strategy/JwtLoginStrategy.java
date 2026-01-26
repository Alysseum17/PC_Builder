package com.pcbuilder.core.modules.auth.strategy;

import com.pcbuilder.core.modules.auth.dto.AuthResult;
import com.pcbuilder.core.modules.auth.dto.JwtResponse;
import com.pcbuilder.core.modules.auth.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class JwtLoginStrategy implements UserLoginStrategy {
    static private final int AUTH_TOKEN_EXPIRY = 15 * 60;
    static private final int REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60;
    @Override
    public boolean supports(AuthResult result) {
        return result instanceof JwtResponse;
    }

    @Override
    public ResponseEntity<?> handle(AuthResult result, HttpServletResponse response) {
        JwtResponse jwtResponse = (JwtResponse) result;

        CookieUtils.addCookie(response, "authToken", jwtResponse.getAuthToken(), AUTH_TOKEN_EXPIRY);
        CookieUtils.addCookie(response, "refreshToken", jwtResponse.getRefreshToken(), REFRESH_TOKEN_EXPIRY);

        return ResponseEntity.ok("You have been logged in");
    }
}
