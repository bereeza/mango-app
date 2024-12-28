package com.mango.mangocompanyservice.dto.vacancy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyStatisticDto {
    private long id;
    private long vacancyId;
    private long views;
    private long applicants;
}
