package com.pcbuilder.core.modules.auth.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class LoginValidator implements ConstraintValidator<Login, String> {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]{3,50}$";

    private final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private final Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        return emailPattern.matcher(value).matches() || usernamePattern.matcher(value).matches();
    }
}