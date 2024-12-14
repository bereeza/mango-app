package com.mango.postservice.repository;

import com.mango.postservice.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepository extends ReactiveCrudRepository<Post, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE mango.post SET text = :text WHERE id = :id")
    Mono<Void> updateText(Long id, String text);

    Flux<Post> findAllBy(Pageable pageable);
}
