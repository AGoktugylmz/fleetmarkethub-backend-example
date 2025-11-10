package com.cosmosboard.fmh.dto.validator;

import com.cosmosboard.fmh.dto.annotation.UUID;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UUIDValidator implements ConstraintValidator<UUID, String> {

    @Override
    public boolean isValid(String userID, ConstraintValidatorContext constraintValidatorContext) {
        if (userID != null && !userID.isEmpty()) {
            try {
                java.util.UUID.fromString(userID);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
}