package backend.mangoapp.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
}
