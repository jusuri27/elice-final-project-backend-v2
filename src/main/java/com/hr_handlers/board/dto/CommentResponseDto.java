package com.hr_handlers.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String content;
    private Long employeeId;
    private String employeeName;
    private Timestamp createdAt;
    private Long parentId;
    private Integer level;

    @Builder.Default
    private List<CommentResponseDto> replies = new ArrayList<>();

}
