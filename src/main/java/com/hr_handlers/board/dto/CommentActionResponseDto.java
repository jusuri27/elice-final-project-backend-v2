package com.hr_handlers.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//댓글/대댓글 생성/수정 후 간단한 응답
public class CommentActionResponseDto {
    private Long id;
    private String timestamp; // createdAt 또는 updatedAt
}
