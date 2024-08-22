package com.mango.mangogatewayservice.exception;

public class InvalidUserDataException extends RuntimeException {
    public InvalidUserDataException() {
    }

    public InvalidUserDataException(String message) {
        super(message);
    }
}
