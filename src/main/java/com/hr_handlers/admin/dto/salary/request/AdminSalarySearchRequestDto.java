package com.hr_handlers.admin.dto.salary.request;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class AdminSalarySearchRequestDto {
    private String deptName;
    private String position;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @AssertTrue(message = "시작일은 종료일 이전이거나 같아야 합니다.")
    public boolean isValidDateRange() {
        if (startDate != null && endDate != null) {
            return !startDate.isAfter(endDate); // 시작일이 종료일보다 이후이면 false 반환
        }
        return true;
    }
}
