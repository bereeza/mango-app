package com.mango.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSaveDto {
    private String text;
    private FilePart file;
}
