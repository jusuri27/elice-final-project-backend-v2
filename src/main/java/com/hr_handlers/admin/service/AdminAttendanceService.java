package com.hr_handlers.admin.service;


import com.hr_handlers.admin.dto.attendance.AdminAttendanceResponseDto;
import com.hr_handlers.admin.dto.attendance.AdminAttendanceSearchDto;
import com.hr_handlers.admin.repository.attendance.AdminAttendanceRepository;
import com.hr_handlers.global.dto.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAttendanceService {

    private final AdminAttendanceRepository adminAttendanceRepository;

    public SuccessResponse<Page<AdminAttendanceResponseDto>> getAllAttendance(
            AdminAttendanceSearchDto searchDto,
            Pageable pageable
    ) {
        return SuccessResponse.of(
                "전체 출퇴근 기록 조회 성공",
                adminAttendanceRepository.findAllAttendance(searchDto, pageable)
        );
    }
}
