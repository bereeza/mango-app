package com.mango.postservice.repository;

import com.mango.postservice.entity.Comment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CommentRepository extends ReactiveCrudRepository<Comment, Long> {
}
