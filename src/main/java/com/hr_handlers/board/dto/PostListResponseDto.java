package com.hr_handlers.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostListResponseDto {
    private List<PostResponseDto> posts; // 게시글 목록
    private long totalElements;         // 전체 게시글 수
}
