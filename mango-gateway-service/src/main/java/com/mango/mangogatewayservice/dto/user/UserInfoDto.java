package com.mango.mangogatewayservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto implements Serializable {
    private long id;
    private String email;
    private String avatar;
}
