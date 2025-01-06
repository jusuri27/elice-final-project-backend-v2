package com.hr_handlers.attendance.service;

import com.hr_handlers.attendance.dto.*;
import com.hr_handlers.attendance.entity.Attendance;
import com.hr_handlers.attendance.entity.AttendanceStatus;
import com.hr_handlers.attendance.mapper.AttendanceMapper;
import com.hr_handlers.attendance.repository.AttendanceRepository;
import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.employee.repository.EmployeeRepository;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;

    private final EmployeeRepository empRepository;


    public SuccessResponse<EmployeeAttendanceResponseDto> getAttendance(String empNo){

        return SuccessResponse.of(
                "사원 출퇴근 시간 조회 성공",
                attendanceRepository.findAttendance(empNo)
        );
    }

    public SuccessResponse<Page<AttendanceHistoryResponseDto>> getAttendanceHistory(
            String empNo,
            AttendanceHistorySearchDto searchDto,
            Pageable pageable
    ) {
        Page<AttendanceHistoryResponseDto> response = attendanceRepository.findAttendanceHistory(
                empNo,
                searchDto,
                pageable
        );

        return SuccessResponse.of(
                "출퇴근 기록 조회 성공",
                response
        );
    }

    public SuccessResponse<CheckInResponseDto> checkIn(String empNo){
        Employee employee = empRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));

        Attendance attendance = Attendance.builder()
                .status(AttendanceStatus.WORK)
                .checkInTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .employee(employee)
                .build();

        attendanceRepository.save(attendance);

        return SuccessResponse.of(
                "출근 성공",
                attendanceMapper.toCheckInResponse(attendance)
        );
    }

    public SuccessResponse<CheckOutResponseDto> checkOut(Long id){
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.ATTENDANCE_NOT_FOUND));

        attendance.modify();

        attendanceRepository.save(attendance);

        return SuccessResponse.of(
                "퇴근 성공",
                attendanceMapper.toCheckOutResponse(attendance)
        );
    }
}
