package com.hr_handlers.admin.dto.attendance;

import com.hr_handlers.attendance.entity.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminAttendanceResponseDto {

    private Long id;
    private String position; // 직위
    private String deptName; // 부서명
    private String name; // 이름
    private AttendanceStatus status; // 출근 상태
    private String checkInTime; // 출근 시간
    private String checkOutTime; // 퇴근 시간
    private Integer workingTime; // 총 근무 시간
}
