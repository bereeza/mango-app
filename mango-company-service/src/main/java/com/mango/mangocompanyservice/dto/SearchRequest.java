package com.mango.mangocompanyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class SearchRequest {
    private String name;
    private int page;
    private int size;
}
