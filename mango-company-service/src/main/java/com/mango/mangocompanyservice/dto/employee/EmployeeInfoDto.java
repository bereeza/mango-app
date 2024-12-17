package com.mango.mangocompanyservice.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInfoDto {
    private long userId;
    private String role;
    private String firstName;
    private String lastName;
    private String avatar;
}
