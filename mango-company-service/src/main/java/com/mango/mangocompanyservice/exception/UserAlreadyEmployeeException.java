package com.mango.mangocompanyservice.exception;

public class UserAlreadyEmployeeException extends RuntimeException {
    public UserAlreadyEmployeeException(String message) {
        super(message);
    }
}
