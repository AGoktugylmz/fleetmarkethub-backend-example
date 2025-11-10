package com.cosmosboard.fmh.dto.annotation;

import com.cosmosboard.fmh.dto.validator.UniqueUserEmailValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueUserEmailValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUserEmail {
    String message() default "E-mail is already using!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
