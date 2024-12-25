package com.mango.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRedisInfo implements Serializable {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;
    private String cv;
}
