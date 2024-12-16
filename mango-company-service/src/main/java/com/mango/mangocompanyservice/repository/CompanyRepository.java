package com.mango.mangocompanyservice.repository;

import com.mango.mangocompanyservice.dto.company.CompanySearchDto;
import com.mango.mangocompanyservice.entity.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CompanyRepository extends ReactiveCrudRepository<Company, Long> {

    @Transactional(readOnly = true)
    @Query("""
           SELECT id, name, logo
           FROM mango.company
           WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))
           LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}
           """)
    Flux<CompanySearchDto> searchByCompanyName(String name, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE mango.company SET website = :website WHERE id = :id")
    Mono<Void> updateWebsite(Long id, String website);

    @Modifying
    @Transactional
    @Query("UPDATE mango.company SET logo = :logo WHERE id = :id")
    Mono<Void> updateLogo(Long id, String logo);
}
