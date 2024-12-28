package com.mango.mangocompanyservice.dto.vacancy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyApplicantsDto {
    private long userId;
    private String coverLetter;
    private String userCv;
    private LocalDateTime applicationDate;
}
