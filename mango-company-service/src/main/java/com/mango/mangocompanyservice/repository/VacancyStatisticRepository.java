package com.mango.mangocompanyservice.repository;

import com.mango.mangocompanyservice.dto.vacancy.VacancyStatisticDto;
import com.mango.mangocompanyservice.entity.VacancyStatistic;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

public interface VacancyStatisticRepository extends ReactiveCrudRepository<VacancyStatistic, Long> {
    @Transactional(readOnly = true)
    @Query("""
            SELECT id, vacancy_id, views, applicants 
            FROM mango.vacancy_statistic WHERE vacancy_id = :id""")
    Mono<VacancyStatisticDto> findStatisticByVacancyId(long id);
}
