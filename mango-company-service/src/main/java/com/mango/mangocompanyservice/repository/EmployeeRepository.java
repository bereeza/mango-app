package com.mango.mangocompanyservice.repository;

import com.mango.mangocompanyservice.dto.employee.EmployeeInfoDto;
import com.mango.mangocompanyservice.entity.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {

    @Modifying
    @Transactional
    @Query("""
            UPDATE mango.company_employees
            SET user_role = :role
            WHERE company_id = :companyId AND user_id = :userId
            """)
    Mono<Integer> updateEmployeeRole(long companyId, long userId, String role);

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM mango.company_employees
            WHERE user_id = :userId
            """)
    Mono<Void> deleteByUserId(long userId);

    @Transactional(readOnly = true)
    @Query("""
            SELECT
            user_id, user_role, first_name, last_name, avatar   
            FROM mango.company_employees
            WHERE company_id = :companyId
            LIMIT :#{#pageable.pageSize}
            OFFSET :#{#pageable.offset}    
            """)
    Flux<EmployeeInfoDto> findAllBy(long companyId, Pageable pageable);
}