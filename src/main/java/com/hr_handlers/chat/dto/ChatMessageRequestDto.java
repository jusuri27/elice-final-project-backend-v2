package com.hr_handlers.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatMessageRequestDto {
    private Long messageId;
    private Long chatRoomId;
    private String empNo;
    private String message;

}
