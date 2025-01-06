package com.hr_handlers.salary.repository.mapper;

import com.hr_handlers.salary.dto.request.SalaryExcelDownloadRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SalaryMapper {

    List<SalaryExcelDownloadRequestDto> getMonthlySalarySummary(@Param("employeeNumber") String employeeNumber);
}
