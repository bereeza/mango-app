package com.mango.mangocompanyservice.entity;

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
@Table(name = "company_employees", schema = "mango")
public class Employee {
    @Id
    @Column(value = "id")
    private long id;

    @Column(value = "company_id")
    private long companyId;

    @Column(value = "user_id")
    private long userId;

    @Column(value = "user_role")
    private String role;

    @Column(value = "first_name")
    private String firstName;

    @Column(value = "last_name")
    private String lastName;

    @Column(value = "avatar")
    private String avatar;
}
