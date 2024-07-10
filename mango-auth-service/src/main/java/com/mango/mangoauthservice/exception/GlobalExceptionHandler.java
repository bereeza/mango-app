package com.mango.mangoauthservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({UserExistsException.class, InvalidUserDataException.class})
    public ResponseEntity<ErrorResponse> handleUserExistsException(RuntimeException ex) {
        ErrorResponse res = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
    }
}
