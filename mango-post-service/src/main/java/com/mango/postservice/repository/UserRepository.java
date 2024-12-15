package com.mango.postservice.repository;

import com.mango.postservice.entity.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE mango.users SET reputation = reputation + :reputation WHERE id = :id")
    Mono<Integer> updateReputation(Long id, Long reputation);
}

