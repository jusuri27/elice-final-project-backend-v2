package com.hr_handlers.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
//게시글 생성/수정 후 간단한 응답
@Data
@Builder
@AllArgsConstructor
public class PostActionResponseDto {
    private Long id;
    private String timestamp; // createdAt 또는 updatedAt
}
