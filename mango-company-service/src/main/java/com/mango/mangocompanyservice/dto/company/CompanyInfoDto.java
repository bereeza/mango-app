package com.mango.mangocompanyservice.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInfoDto {
    private long id;
    private String name;
    private String description;
    private String website;
    private String logo;
    private LocalDateTime createdAt;
    private long ceoId;
}
