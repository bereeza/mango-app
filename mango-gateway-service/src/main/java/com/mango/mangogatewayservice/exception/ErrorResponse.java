package com.mango.mangogatewayservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int code;
    private String message;
}
