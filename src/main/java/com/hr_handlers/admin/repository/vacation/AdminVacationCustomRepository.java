package com.hr_handlers.admin.repository.vacation;

import com.hr_handlers.admin.dto.vacation.AdminVacationResponseDto;
import com.hr_handlers.admin.dto.vacation.AdminVacationStatusResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminVacationCustomRepository {

    Page<AdminVacationResponseDto> findPendingVacations(Pageable pageable);

    Page<AdminVacationStatusResponseDto> findVacationStatusForAllEmployees(Pageable pageable);
}
