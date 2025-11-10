package com.cosmosboard.fmh.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {
    String[] roles() default {"ADMIN", "USER", "CONSULTANT"};
    boolean isOwer() default false;
}