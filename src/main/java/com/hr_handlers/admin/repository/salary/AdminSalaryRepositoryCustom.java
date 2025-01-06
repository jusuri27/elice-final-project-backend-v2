package com.hr_handlers.admin.repository.salary;

import com.hr_handlers.admin.dto.salary.request.AdminSalaryExcelDownloadRequestDto;
import com.hr_handlers.admin.dto.salary.request.AdminSalarySearchRequestDto;
import com.hr_handlers.admin.dto.salary.response.AdminSalaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminSalaryRepositoryCustom {
    List<AdminSalaryResponseDto> findAllSalary();

    Page<AdminSalaryResponseDto> searchSalaryByFilter(Pageable pageable, AdminSalarySearchRequestDto adminSalarySearchRequestDto);
    List<AdminSalaryExcelDownloadRequestDto> searchSalaryForExcel(AdminSalarySearchRequestDto adminSalarySearchRequestDto);
}
