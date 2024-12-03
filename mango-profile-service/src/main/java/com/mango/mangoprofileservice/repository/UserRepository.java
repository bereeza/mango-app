package com.mango.mangoprofileservice.repository;

import com.mango.mangoprofileservice.entity.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE mango.users SET link = :link WHERE id = :id")
    Mono<Integer> updateLinkAtIndex(String link, Long id);

    @Modifying
    @Query("UPDATE mango.users SET about = :about WHERE id = :id")
    Mono<Void> updateUserAbout(@Param("id") long id, @Param("about") String about);
}

