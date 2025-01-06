package com.hr_handlers.attendance.repository;

import com.hr_handlers.attendance.dto.AttendanceHistoryResponseDto;
import com.hr_handlers.attendance.dto.AttendanceHistorySearchDto;
import com.hr_handlers.attendance.dto.EmployeeAttendanceResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttendanceCustomRepository {

    EmployeeAttendanceResponseDto findAttendance(String empNo);

    Page<AttendanceHistoryResponseDto> findAttendanceHistory(
            String empNo,
            AttendanceHistorySearchDto searchDto,
            Pageable pageable
    );
}
