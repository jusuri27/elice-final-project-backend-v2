package com.hr_handlers.admin.controller;

import com.hr_handlers.admin.dto.attendance.AdminAttendanceResponseDto;
import com.hr_handlers.admin.dto.attendance.AdminAttendanceSearchDto;
import com.hr_handlers.admin.service.AdminAttendanceService;
import com.hr_handlers.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/attendance")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "어드민 근태관리", description = "어드민 근태관리 API")
public class AdminAttendanceController {

    private final AdminAttendanceService adminAttendanceService;

    // 전 사원 출퇴근 조회
    @GetMapping
    @Operation(summary = "모든 사원 출퇴근 기록 조회", description = "모든 사원 출퇴근 기록 조회")
    public SuccessResponse<Page<AdminAttendanceResponseDto>> getAdminAttendance(
            @RequestParam(required = false, value = "checkInTime")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime checkInTime,
            @RequestParam(required = false, value = "checkOutTime")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime checkOutTime,
            @RequestParam(required = false, value = "deptName") String deptName,
            @RequestParam(required = false, value = "position") String position,
            @RequestParam(required = false, value = "name") String name,
            @PageableDefault(size = 10, sort = "checkInTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        AdminAttendanceSearchDto searchDto = new AdminAttendanceSearchDto(
                checkInTime, checkOutTime, deptName, position, name
        );
        return adminAttendanceService.getAllAttendance(searchDto, pageable);
    }
}
