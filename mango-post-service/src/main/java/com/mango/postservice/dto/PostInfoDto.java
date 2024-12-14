package com.mango.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostInfoDto implements Serializable {
    private long id;
    private long userId;
    private String text;
    private String photoLink;
    private long reputation;
}
