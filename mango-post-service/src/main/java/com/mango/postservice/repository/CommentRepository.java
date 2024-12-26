package com.mango.postservice.repository;

import com.mango.postservice.dto.comment.CommentInfoDto;
import com.mango.postservice.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

public interface CommentRepository extends ReactiveCrudRepository<Comment, Long> {

    @Transactional(readOnly = true)
    @Query("""
        SELECT *
        FROM mango.comment c
        JOIN mango.post p ON c.post_id = p.id
        WHERE p.id = :postId
        ORDER BY c.created_at
        LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}
    """)
    Flux<CommentInfoDto> findAllBy(Long postId, Pageable pageable);
}

