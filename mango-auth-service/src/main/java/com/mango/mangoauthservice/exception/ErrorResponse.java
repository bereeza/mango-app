package com.mango.mangoauthservice.exception;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorResponse {
    private int status;
    private String message;
}
