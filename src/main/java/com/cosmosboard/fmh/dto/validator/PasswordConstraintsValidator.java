package com.cosmosboard.fmh.dto.validator;

import com.cosmosboard.fmh.dto.annotation.Password;
import com.cosmosboard.fmh.util.AppUtil;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PasswordConstraintsValidator implements ConstraintValidator<Password, String> {
    private boolean detailedMessage;

    @Override
    public void initialize(Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        detailedMessage = constraintAnnotation.detailedMessage();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        final List<String> passwordValid = AppUtil.isPasswordValid(password);
        if (passwordValid.isEmpty())
            return true;
        if (detailedMessage) {
            final String messageTemplate = String.join("\n", passwordValid);
            context.buildConstraintViolationWithTemplate(messageTemplate)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }
        return false;
    }
}
