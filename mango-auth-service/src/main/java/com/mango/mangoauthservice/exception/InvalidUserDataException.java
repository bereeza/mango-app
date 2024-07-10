package com.mango.mangoauthservice.exception;

public class InvalidUserDataException extends RuntimeException {
    public InvalidUserDataException() {
    }

    public InvalidUserDataException(String message) {
        super(message);
    }
}
