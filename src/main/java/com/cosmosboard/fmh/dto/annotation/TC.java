package com.cosmosboard.fmh.dto.annotation;

import com.cosmosboard.fmh.dto.validator.TcValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TcValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TC {
    String message() default "The TC not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
