package com.mango.mangogatewayservice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class AuthRequest {
    private String email;
    private String password;
}
