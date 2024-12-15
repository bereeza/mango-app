package com.mango.mangocompanyservice.repository;

import com.mango.mangocompanyservice.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
}

