package com.mango.mangogatewayservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_user")
public class User {
    @Id
    private long userid;
    private String email;
    private String password;
    private String nickname;
}
