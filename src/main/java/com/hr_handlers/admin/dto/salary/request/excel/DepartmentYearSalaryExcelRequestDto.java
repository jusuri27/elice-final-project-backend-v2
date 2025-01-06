package com.hr_handlers.admin.dto.salary.request.excel;

import com.hr_handlers.global.utils.ExcelColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DepartmentYearSalaryExcelRequestDto {
    @ExcelColumn(headerName = "부서", sort=1)
    private String deptName;
    @ExcelColumn(headerName = "연도", sort=2)
    private String year;
    @ExcelColumn(headerName = "지급총액", sort=3)
    private int basicSalary;
    @ExcelColumn(headerName = "공제총액", sort=4)
    private int deduction;
    @ExcelColumn(headerName = "실지급액", sort=5)
    private int netSalary;
}
