package com.cosmosboard.fmh.dto.validator;

import com.cosmosboard.fmh.dto.annotation.DateFormat;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;

public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {
    private String format;

    @Override
    public void initialize(DateFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        format = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null || date.isEmpty()) {
            return true;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setLenient(false);

        try {
            simpleDateFormat.parse(date);
        } catch (ParseException | DateTimeException e) {
            return false;
        }

        return true;
    }
}
