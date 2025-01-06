package com.hr_handlers.salary.service;

import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.utils.ExcelUploadUtils;
import com.hr_handlers.salary.dto.request.SalaryExcelDownloadRequestDto;
import com.hr_handlers.salary.dto.response.SalaryResponse;
import com.hr_handlers.salary.repository.SalaryRepository;
import com.hr_handlers.salary.repository.mapper.SalaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final ExcelUploadUtils excelUploadUtils;

    private final SalaryMapper salaryMapper;

    public SuccessResponse<Page<SalaryResponse>> getSalaryByUser(Pageable pageable, String employeeNumber) {
        return SuccessResponse.of("급여 관리 조회 성공", salaryRepository.findSalaryByEmployeeNumber(pageable, employeeNumber));
    }


    public SuccessResponse<Boolean> excelDownloadSalary(OutputStream stream, String employeeNumber) throws IOException, IllegalAccessException {
        excelUploadUtils.renderObjectToExcel(stream, salaryMapper.getMonthlySalarySummary(employeeNumber), SalaryExcelDownloadRequestDto.class);
        return SuccessResponse.of("성공적으로 다운로드 되었습니다.", true);
    }

}
