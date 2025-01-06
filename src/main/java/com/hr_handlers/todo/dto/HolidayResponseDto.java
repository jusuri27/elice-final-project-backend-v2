package com.hr_handlers.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HolidayResponseDto {

    private String locdate;
    private String dateName;
    private boolean isHoliday;
}
