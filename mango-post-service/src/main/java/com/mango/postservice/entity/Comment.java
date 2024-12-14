package com.mango.postservice.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment", schema = "mango")
public class Comment {

    @Id
    @Column(value = "id")
    private long id;

    @Column(value = "post_id")
    private long postId;

    @Column(value = "author_first_name")
    private String firstName;

    @Column(value = "author_last_name")
    private String lastName;

    @Column(value = "author_avatar")
    private String avatar;

    @Column(value = "comment")
    private String comment;

    @CreatedDate
    @Column(value = "created_at")
    private LocalDateTime createdAt;
}
