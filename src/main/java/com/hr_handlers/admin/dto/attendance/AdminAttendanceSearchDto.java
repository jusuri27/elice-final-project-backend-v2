package com.hr_handlers.admin.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminAttendanceSearchDto {
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String deptName;    // 부서
    private String position;      // 직위
    private String name;  // 사원 이름
}
