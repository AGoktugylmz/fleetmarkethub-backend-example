package com.cosmosboard.fmh.exception;

public class StorageException extends RuntimeException {
    public StorageException() {
        super("Storage exception!");
    }

    public StorageException(String message) {
        super(message);
    }
}
