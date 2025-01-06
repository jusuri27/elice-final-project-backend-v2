package com.hr_handlers.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceHistoryResponseDto {
    private String date;       // 날짜
    private String checkInTime;  // 출근 시간
    private String checkOutTime; // 퇴근 시간
    private Integer workingTime;  // 총 근무 시간
}
