package com.mango.mangogatewayservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UserSaveDto {
    private long id;
    private String email;
}
