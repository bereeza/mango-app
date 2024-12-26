package com.mango.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentInfoDto {
    private long id;
    private long postId;
    private String firstName;
    private String lastName;
    private String avatar;
    private String comment;
    private LocalDateTime createdAt;
}
