package com.cosmosboard.fmh.dto.annotation;

import com.cosmosboard.fmh.dto.validator.FileCheckValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {FileCheckValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RUNTIME)
public @interface FileCheck {
    String message() default "Invalid file type";

    String maxSizeMessage() default "File size is too large";

    String[] contentTypes() default {"image/png", "image/jpeg", "image/jpg"};

    long maxSize() default -1L;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
