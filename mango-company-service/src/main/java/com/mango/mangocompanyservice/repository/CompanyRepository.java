package com.mango.mangocompanyservice.repository;

import com.mango.mangocompanyservice.entity.Company;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CompanyRepository extends ReactiveCrudRepository<Company, Long> {
}
