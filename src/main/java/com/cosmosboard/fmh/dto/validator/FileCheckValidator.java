package com.cosmosboard.fmh.dto.validator;

import com.cosmosboard.fmh.dto.annotation.FileCheck;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class FileCheckValidator implements ConstraintValidator<FileCheck, MultipartFile> {
    private String maxSizeMessage;

    private String[] contentTypes;

    private long maxSize;

    @Override
    public void initialize(FileCheck constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        maxSizeMessage = constraintAnnotation.maxSizeMessage();
        contentTypes = constraintAnnotation.contentTypes();
        maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        if (multipartFile == null) {
            return true;
        }

        String contentType = multipartFile.getContentType();

        if (contentType == null) {
            return false;
        }

        if (maxSize > -1L && multipartFile.getSize() > maxSize) {
            context.buildConstraintViolationWithTemplate(maxSizeMessage)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
            return false;
        }

        return Arrays.asList(contentTypes).contains(contentType);
    }
}
