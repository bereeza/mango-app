package com.mango.mangocompanyservice.dto.vacancy;

import com.mango.mangocompanyservice.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancySaveDto {
    private boolean isAnonymous;
    private String title;
    private String description;
    private Type type;
    private String location;
    private BigDecimal salary;
}
