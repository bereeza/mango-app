package com.mango.mangoprofileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class UserInfoDto implements Serializable {
    private long id;

    private String email;

    private String username;
}
