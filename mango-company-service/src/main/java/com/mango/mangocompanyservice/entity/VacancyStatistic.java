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
@Table(name = "vacancy_statistic", schema = "mango")
public class VacancyStatistic {
    @Id
    @Column(value = "id")
    private long id;

    @Column(value = "vacancy_id")
    private long vacancyId;

    @Column(value = "views")
    private long views;

    @Column(value = "applicants")
    private long applicants;
}
