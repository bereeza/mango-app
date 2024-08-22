package com.mango.mangogatewayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Response {
    private String message;
    private int code;
}
