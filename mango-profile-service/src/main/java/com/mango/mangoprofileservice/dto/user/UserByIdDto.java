package com.mango.mangoprofileservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserByIdDto implements Serializable {
    private long id;
    private String firstName;
    private String lastName;
    private String avatar;
    private String cv;
    private String about;
    private String reputation;
    private String link;
}