package com.pcbuilder.core.modules.auth.strategy;

import com.pcbuilder.core.modules.auth.dto.AuthResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginResponseHandler {
    private final List<UserLoginStrategy> userLoginStrategies;

    public ResponseEntity<?> handleLoginResponse(AuthResult result, HttpServletResponse response) {
        var strategyOptional = userLoginStrategies.stream()
                .filter(strategy -> strategy.supports(result))
                .findFirst();

        if (strategyOptional.isPresent()) {
            return strategyOptional.get().handle(result, response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid credentials");
    }
}
