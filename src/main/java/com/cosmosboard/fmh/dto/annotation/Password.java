package com.cosmosboard.fmh.dto.annotation;

import com.cosmosboard.fmh.dto.validator.PasswordConstraintsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordConstraintsValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface Password {
    String message() default "Invalid password.";

    boolean detailedMessage() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
