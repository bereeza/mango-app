package com.mango.mangocompanyservice.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanySearchDto {
    private long id;
    private String name;
    private String logo;
}
