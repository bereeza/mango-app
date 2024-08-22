package com.mango.mangogatewayservice.exception;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ErrorResponse {
    private int status;
    private String message;
}
