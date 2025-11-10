package com.cosmosboard.fmh.exception;

public class StorageEmptyFileException extends StorageException {
    public StorageEmptyFileException() {
        super("Failed to store empty file!");
    }

    public StorageEmptyFileException(String message) {
        super(message);
    }
}
