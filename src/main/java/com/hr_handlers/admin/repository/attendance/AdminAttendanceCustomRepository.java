package com.hr_handlers.admin.repository.attendance;

import com.hr_handlers.admin.dto.attendance.AdminAttendanceResponseDto;
import com.hr_handlers.admin.dto.attendance.AdminAttendanceSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminAttendanceCustomRepository {

    // 전체 출퇴근 기록 조회
    Page<AdminAttendanceResponseDto> findAllAttendance(AdminAttendanceSearchDto searchDto,
                                                       Pageable pageable);
}
