package com.pcbuilder.core.modules.auth.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = LoginValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {

    String message() default "Must be a valid email or username (3-50 chars)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
