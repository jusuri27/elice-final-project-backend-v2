package com.hr_handlers.attendance.dto;

import com.hr_handlers.attendance.entity.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponseDto {

    private Long id;

    private AttendanceStatus status;

    private LocalDateTime checkInTime;

    private Long employeeId;
}
