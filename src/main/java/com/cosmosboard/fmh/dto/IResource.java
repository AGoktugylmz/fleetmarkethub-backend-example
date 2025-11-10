package com.cosmosboard.fmh.dto;

import com.cosmosboard.fmh.exception.BadRequestException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface IResource<T> {
    default void validate() {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        final Set<ConstraintViolation<T>> violations = validator.validate((T) this);

        if (!violations.isEmpty()) {
            List<String> exceptionMessageList = new ArrayList<>();
            for (ConstraintViolation<T> violation : violations) {
                exceptionMessageList.add("Property-Path: " + violation.getPropertyPath() + ", Message: " + violation.getMessage());
            }
            throw new BadRequestException(exceptionMessageList.toString());
        }
    }
}
