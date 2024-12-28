package com.mango.mangocompanyservice.dto.vacancy;

import com.mango.mangocompanyservice.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyInfoDto {
    private long id;
    private long userId;
    private boolean isAnonymous;
    private long companyId;
    private String title;
    private String description;
    private Type type;
    private String location;
    private BigDecimal salary;
    private LocalDateTime createdAt;
}
