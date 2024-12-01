package com.mango.mangoprofileservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_user", schema = "mango")
public class User {
    @Id
    @Column(value = "id")
    private long id;

    @Column(value = "email")
    private String email;

    @Column(value = "first_name")
    private String firstName;

    @Column(value = "last_name")
    private String lastName;

    @Column(value = "password")
    private String password;

    @Column(value = "avatar")
    private String avatar;

    @Column(value = "cv")
    private String cv;

    @Column(value = "about")
    private String about;

    @Column(value = "reputation")
    private String reputation;

    @Column(value = "links")
    private String[] links;
}
