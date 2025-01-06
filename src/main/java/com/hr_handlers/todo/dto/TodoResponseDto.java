package com.hr_handlers.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDto {


    private Long id;

    private String title;

    private String content;

    private Timestamp startTime;

    private Timestamp endTime;

    private Long employeeId;

}
