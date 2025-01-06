package com.hr_handlers.attendance.controller;

import com.hr_handlers.attendance.dto.*;
import com.hr_handlers.attendance.service.AttendanceService;
import com.hr_handlers.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Tag(name = "근태", description = "사원 근태 API")
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 출 퇴근 조회
    @GetMapping
    @Operation(summary = "출,퇴근 시간 조회", description = "홈 화면에 나타낼 출,퇴근 시간 조회")
    public SuccessResponse<EmployeeAttendanceResponseDto> getAttendance(Authentication authentication){
        return attendanceService.getAttendance(authentication.getName());
    }

    @GetMapping("/history")
    @Operation(summary = "출,퇴근 기록 조회", description = "로그인한 사원 출 퇴근 기록 페이지네이션으로 조회")
    public SuccessResponse<Page<AttendanceHistoryResponseDto>> getAttendanceHistory
    (
        Authentication authentication,
        @RequestParam(required = false, value = "checkInTime")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime checkInTime,
        @RequestParam(required = false, value = "checkOutTime")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime checkOutTime,
        @PageableDefault(size = 10, sort = "checkInTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        AttendanceHistorySearchDto searchDto = new AttendanceHistorySearchDto(checkInTime, checkOutTime);
        return attendanceService.getAttendanceHistory(
                authentication.getName(),
                searchDto,
                pageable
        );
    }

    // 출근
    @PostMapping
    @Operation(summary = "출근 처리", description = "출근 처리")
    public SuccessResponse<CheckInResponseDto> checkIn(Authentication authentication){
        return attendanceService.checkIn(authentication.getName());
    }

    // 퇴근
    @PutMapping("/{id}")
    @Operation(summary = "퇴근 처리", description = "퇴근 처리")
    public SuccessResponse<CheckOutResponseDto> checkOut(@PathVariable("id") Long id){
        return attendanceService.checkOut(id);
    }
}
