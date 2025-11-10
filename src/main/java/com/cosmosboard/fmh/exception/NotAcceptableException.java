package com.cosmosboard.fmh.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotAcceptableException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NotAcceptableException() {
        super("Not acceptable!");
    }

    public NotAcceptableException(String message) {
        super(message);
    }
}
