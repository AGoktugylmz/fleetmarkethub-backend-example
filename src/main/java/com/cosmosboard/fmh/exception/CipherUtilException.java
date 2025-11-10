package com.cosmosboard.fmh.exception;

import java.io.Serial;

public class CipherUtilException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CipherUtilException() {
        super("Cipher operation failed!");
    }

    public CipherUtilException(String message) {
        super(message);
    }
}
