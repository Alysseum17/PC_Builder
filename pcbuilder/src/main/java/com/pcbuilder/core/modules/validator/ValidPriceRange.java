package com.pcbuilder.core.modules.validator;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PriceRangeValidator.class)
public @interface ValidPriceRange {
    String message() default "Min price must be less than or equal to max price";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

