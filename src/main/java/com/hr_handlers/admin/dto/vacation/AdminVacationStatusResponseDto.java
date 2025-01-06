package com.hr_handlers.admin.dto.vacation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminVacationStatusResponseDto {
    private String position;      // 직급
    private String deptName;      // 부서
    private String name;          // 이름
    private double annualLeave;   // 연차
    private double halfLeave;     // 반차
    private double sickLeave;     // 병가
    private double publicLeave;   // 공가
    private double totalUsed;     // 총 사용 일수
    private double remainingDays; // 잔여 일수
}
