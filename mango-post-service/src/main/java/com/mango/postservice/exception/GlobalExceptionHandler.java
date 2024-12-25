package com.mango.postservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({PostNotFoundException.class, CommentNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(RuntimeException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AccessForbiddenException.class})
    public ResponseEntity<ErrorResponse> handleAccessForbiddenException(AccessForbiddenException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedUserException(UnauthorizedUserException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
