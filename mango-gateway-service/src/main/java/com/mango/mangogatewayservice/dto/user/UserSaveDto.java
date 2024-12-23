package com.mango.mangogatewayservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSaveDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
