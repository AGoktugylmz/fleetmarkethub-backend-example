package com.cosmosboard.fmh.dto.validator;

import com.cosmosboard.fmh.dto.annotation.FieldMatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstField;

    private String secondField;

    private String message;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstField = constraintAnnotation.first();
        secondField = constraintAnnotation.second();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        boolean valid = true;

        try {
            final Object firstProperty = BeanUtils.getProperty(obj, firstField);
            final Object secondProperty = BeanUtils.getProperty(obj, secondField);

            valid = firstProperty == null && secondProperty == null ||
                firstProperty != null && firstProperty.equals(secondProperty);
        } catch (final Exception e) {
            log.error("Exception: {}", e.getMessage());
        }

        if (!valid) {
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(firstField)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        }

        return valid;
    }
}
