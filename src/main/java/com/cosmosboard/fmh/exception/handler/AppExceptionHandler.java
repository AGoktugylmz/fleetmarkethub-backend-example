package com.cosmosboard.fmh.exception.handler;

import com.cosmosboard.fmh.exception.NotAcceptableException;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.dto.response.ErrorResponse;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.ExpectationException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.exception.StorageEmptyFileException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class AppExceptionHandler {
    private final MessageSourceService messageSourceService;

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.METHOD_NOT_ALLOWED, messageSourceService.get("method_not_supported"), null);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, InvalidFormatException.class})
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(Exception e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.BAD_REQUEST, messageSourceService.get("malformed_json_request"), null);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error(e.toString(), e.getMessage());
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return build(HttpStatus.UNPROCESSABLE_ENTITY, messageSourceService.get("validation_error"), errors);
    }

    @ExceptionHandler({
        BadRequestException.class,
        MultipartException.class,
        MissingServletRequestPartException.class,
        StorageEmptyFileException.class,
        HttpMediaTypeNotSupportedException.class,
        MethodArgumentTypeMismatchException.class,
        IllegalArgumentException.class,
        ConstraintViolationException.class,
        HttpMediaTypeNotAcceptableException.class
    })
    public final ResponseEntity<ErrorResponse> handleBadRequestException(Exception e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.BAD_REQUEST, e.getCause() != null ? e.getCause().getMessage() : e.getMessage(),
            null);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler({
        InternalAuthenticationServiceException.class,
        BadCredentialsException.class,
        AccessDeniedException.class,
        AuthenticationCredentialsNotFoundException.class
    })
    public final ResponseEntity<ErrorResponse> handleBadCredentialsException(Exception e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.UNAUTHORIZED, e.getMessage(), null);
    }

    @ExceptionHandler(ExpectationException.class)
    public final ResponseEntity<ErrorResponse> handleExpectationException(Exception e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.EXPECTATION_FAILED, e.getMessage(), null);
    }

    @ExceptionHandler(NotAcceptableException.class)
    public final ResponseEntity<ErrorResponse> handleNotAcceptableException(Exception e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.NOT_ACCEPTABLE, e.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception e) {
        log.error("Exception: {}", ExceptionUtils.getStackTrace(e));
        return build(HttpStatus.INTERNAL_SERVER_ERROR, messageSourceService.get("server_error"), null);
    }

    /**
     * Build error response
     *
     * @param httpStatus HttpStatus enum to response status field
     * @param message String for response message field
     * @param errors Map for response errors field
     * @return ResponseEntity
     */
    private ResponseEntity<ErrorResponse> build(HttpStatus httpStatus, String message, Map<String, String> errors) {
        ErrorResponse build = ErrorResponse.builder()
            .status(httpStatus.value())
            .message(message)
            .items(errors)
            .build();
        return ResponseEntity.status(httpStatus).body(build);
    }
}
