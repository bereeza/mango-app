package com.mango.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostShortInfoDto {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private Long commentCount;
}
