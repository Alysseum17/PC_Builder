package com.pcbuilder.core.modules.validator;

import com.pcbuilder.core.modules.components.dto.ComponentFilterRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceRangeValidator implements ConstraintValidator<ValidPriceRange, ComponentFilterRequestDto> {
    @Override
    public boolean isValid(ComponentFilterRequestDto dto, ConstraintValidatorContext context) {
        if(dto.getMinPrice() == null || dto.getMaxPrice() == null) {
            return true;
        }
        return dto.getMinPrice().compareTo(dto.getMaxPrice()) <= 0;
    }
}
