package com.cosmosboard.fmh.exception;

public class RegistrationTokenExpiredException extends BadRequestException {
    public RegistrationTokenExpiredException() {
        super("Registration token is expired!");
    }

    public RegistrationTokenExpiredException(String message) {
        super(message);
    }
}
