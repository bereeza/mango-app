package com.mango.postservice.exception;

public class UserByTokenNotFound extends RuntimeException {
    public UserByTokenNotFound(String message) {
        super(message);
    }
}
