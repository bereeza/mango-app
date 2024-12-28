package com.mango.mangocompanyservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vacancy_applicants", schema = "mango")
public class Applicant {
    @Id
    @Column(value = "id")
    private long id;

    @Column(value = "vacancy_id")
    private long vacancyId;

    @Column(value = "user_id")
    private long userId;

    @Column(value = "cover_letter")
    private String coverLetter;

    @Column(value = "user_cv")
    private String userCv;

    @CreatedDate
    @Column(value = "application_date")
    private LocalDateTime applicationDate;
}
