package com.mango.mangogatewayservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserAlreadyExistsException.class, InvalidUserDataException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleCustomExceptions(RuntimeException ex) {
        HttpStatus status = getHttpStatus(ex);
        ErrorResponse res = ErrorResponse.builder()
                .status(status.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(res, status);
    }

    private HttpStatus getHttpStatus(RuntimeException ex) {
        if (ex instanceof UserAlreadyExistsException) {
            return HttpStatus.CONFLICT;
        } else if (ex instanceof InvalidUserDataException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof UserNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
