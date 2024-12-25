package com.mango.postservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post", schema = "mango")
public class Post {
    @Id
    @Column(value = "id")
    private long id;

    @Column(value = "user_id")
    private long userId;

    @Column(value = "text")
    private String text;

    @Column(value = "photo_link")
    private String photoLink;

    @CreatedDate
    @Column(value = "created_at")
    private LocalDateTime createdAt;
}
