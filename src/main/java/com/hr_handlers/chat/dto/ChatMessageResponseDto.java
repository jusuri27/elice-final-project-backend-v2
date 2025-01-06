package com.hr_handlers.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatMessageResponseDto {
    private Long messageId;
    private Long chatRoomId;
    private String empNo;
    private String employeeName;
    private String message;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
