package com.mango.mangoprofileservice.repository;

import com.mango.mangoprofileservice.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
}
