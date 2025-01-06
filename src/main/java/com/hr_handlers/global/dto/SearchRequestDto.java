package com.hr_handlers.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {
    private int page = 0;
    private int size = 10;
    private String sortField = "createdAt";
    private String sortDir = "desc";
    private String keyword;
}