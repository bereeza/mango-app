package com.mango.mangocompanyservice.repository;

import com.mango.mangocompanyservice.dto.user.UserInfoDto;
import com.mango.mangocompanyservice.dto.vacancy.VacancyApplicantsDto;
import com.mango.mangocompanyservice.entity.Applicant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VacancyApplicantRepository extends ReactiveCrudRepository<Applicant, Long> {

    @Modifying
    @Query("""
            UPDATE mango.vacancy_statistic
            SET views = views + 1
            WHERE vacancy_id = :vacancyId
            """)
    Mono<Integer> incrementViews(long vacancyId);

    @Modifying
    @Query("""
            UPDATE mango.vacancy_statistic
            SET applicants = applicants + 1
            WHERE vacancy_id = :vacancyId
            """)
    Mono<Integer> incrementApplicants(long vacancyId);

    @Query("""
            SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END 
            FROM mango.vacancy_applicants a 
            WHERE a.user_id = :userId AND a.vacancy_id = :vacancyId
            """)
    Mono<Boolean> existsByUserIdAndVacancyId(long userId, long vacancyId);

    @Transactional(readOnly = true)
    @Query("""
            SELECT user_id, cover_letter, user_cv, application_date
            FROM mango.vacancy_applicants 
            WHERE vacancy_id = :vacancyId
            ORDER BY application_date    
            LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}
            """)
    Flux<VacancyApplicantsDto> findAllApplicantsByVacancyId(long vacancyId, Pageable pageable);
}

