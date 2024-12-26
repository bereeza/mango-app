package com.mango.postservice.repository;

import com.mango.postservice.dto.post.PostInfoDto;
import com.mango.postservice.dto.post.PostShortInfoDto;
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
    Mono<Integer> updateText(Long id, String text);

    @Transactional(readOnly = true)
    @Query("""
            SELECT *
            FROM mango.post
            ORDER BY created_at
            LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}
            """)
    Flux<PostInfoDto> findAllPosts(Pageable pageable);

    @Transactional(readOnly = true)
    @Query("""
            SELECT *
            FROM mango.post
            WHERE post.user_id = :id
            ORDER BY created_at
            LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}
            """)
    Flux<PostInfoDto> findAllUserPosts(Pageable pageable, long id);

    @Transactional(readOnly = true)
    @Query("""
            SELECT *
            FROM mango.post
            WHERE LOWER(text) LIKE LOWER(CONCAT('%', :text, '%'))
            LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}
            """)
    Flux<PostInfoDto> findAllByText(Pageable pageable, String text);

    @Transactional(readOnly = true)
    @Query("""
            SELECT p.*, COUNT(c.id) AS comment_count
            FROM mango.post p
            LEFT JOIN mango.comment c ON p.id = c.post_id
            WHERE c.created_at >= NOW() - INTERVAL '1 month'
            GROUP BY p.id
            ORDER BY comment_count DESC
            LIMIT 5;
            """)
    Flux<PostShortInfoDto> findTopPostsByComments();
}
