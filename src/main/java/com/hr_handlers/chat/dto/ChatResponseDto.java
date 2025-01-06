package com.hr_handlers.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatResponseDto {
    private Long employeeId;
    private Long chatRoomId;
    private String title;
    private String isSecret;
    private String empNo;
    private String empName;
    private String empPosition;
    private String deptName;
}
