package com.mango.mangocompanyservice.repository;

import com.mango.mangocompanyservice.dto.vacancy.VacancyInfoDto;
import com.mango.mangocompanyservice.entity.Vacancy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VacancyRepository extends ReactiveCrudRepository<Vacancy, Long> {
    @Query("""
            SELECT COUNT(*) > 0
            FROM mango.company_employees
            WHERE user_id = :userId AND company_id = :companyId
            """)
    Mono<Boolean> isUserEmployeeOfCompany(long userId, long companyId);

    @Transactional(readOnly = true)
    @Query("""
            SELECT
            id, user_id, is_anonymous, title, description, type, location, salary, created_at
            FROM mango.vacancy
            WHERE company_id = :companyId
            ORDER BY created_at DESC  
            LIMIT :#{#pageable.pageSize}
            OFFSET :#{#pageable.offset}
            """)
    Flux<VacancyInfoDto> findAllBy(long companyId, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("""
       SELECT id, user_id, is_anonymous, company_id, title, description, type, location, salary, created_at
       FROM mango.vacancy
       WHERE (:title IS NULL OR LOWER(title) LIKE LOWER(CONCAT('%', :title, '%')))
       ORDER BY created_at
       LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}
       """)
    Flux<Vacancy> findVacanciesBy(String title, Pageable pageable);
}
