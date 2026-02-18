package com.pcbuilder.core.modules.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class BuildCompatibilityException extends BaseException {

    private final List<String> errors;

    public BuildCompatibilityException(List<String> errors) {
        super("Validation failed with " + errors.size() + " errors: " + String.join(", ", errors), "BUILD_COMPATIBILITY_ERROR");
        this.errors = errors;
    }
}
