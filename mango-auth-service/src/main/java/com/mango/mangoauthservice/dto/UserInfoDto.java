package com.mango.mangoauthservice.dto;

import com.mango.mangoauthservice.user.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoDto {
    private String email;
    private String nickname;
    private Role role;
}
