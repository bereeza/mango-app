package com.mango.mangoprofileservice.repository;

import com.mango.mangoprofileservice.entity.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE mango.users SET link = :link WHERE id = :id")
    Mono<Integer> updateLink(Long id, String link);

    @Modifying
    @Transactional
    @Query("UPDATE mango.users SET about = :about WHERE id = :id")
    Mono<Integer> updateUserAbout(Long id, String about);

    @Modifying
    @Transactional
    @Query("UPDATE mango.users SET cv = :about WHERE id = :id")
    Mono<Integer> updateUserCV(Long id, String cv);
}

