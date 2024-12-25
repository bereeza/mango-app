package com.mango.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostInfoDto implements Serializable {
    private long id;
    private long userId;
    private String text;
    private String photoLink;
    private LocalDateTime createdAt;
}
