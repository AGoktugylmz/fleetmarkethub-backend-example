package com.cosmosboard.fmh.exception;

public class RefreshTokenExpiredException extends BadRequestException {
    public RefreshTokenExpiredException() {
        super("Refresh token is expired!");
    }

    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
