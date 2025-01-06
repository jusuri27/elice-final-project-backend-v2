package com.hr_handlers.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatRoomRequestDto {
    private String title;
    private String isSecret;
}
