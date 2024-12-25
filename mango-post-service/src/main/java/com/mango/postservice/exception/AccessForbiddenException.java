package com.mango.postservice.exception;

public class AccessForbiddenException extends RuntimeException {
    public AccessForbiddenException(String message) {
        super(message);
    }
}
