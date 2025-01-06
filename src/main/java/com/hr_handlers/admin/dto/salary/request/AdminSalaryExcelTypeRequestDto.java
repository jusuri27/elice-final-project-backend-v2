package com.hr_handlers.admin.dto.salary.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AdminSalaryExcelTypeRequestDto {
    @NotNull(message = "타입을 설정해주세요.")
    private String downloadScope;

    @NotNull(message = "타입을 설정해주세요.")
    private String timePeriod;
}
