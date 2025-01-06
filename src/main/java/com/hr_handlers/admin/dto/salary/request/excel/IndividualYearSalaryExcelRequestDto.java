package com.hr_handlers.admin.dto.salary.request.excel;

import com.hr_handlers.global.utils.ExcelColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IndividualYearSalaryExcelRequestDto {
    @ExcelColumn(headerName = "부서", sort=1)
    private String deptName;
    @ExcelColumn(headerName = "이름", sort=2)
    private String name;
    @ExcelColumn(headerName = "연도", sort=3)
    private String year;
    @ExcelColumn(headerName = "지급총액", sort=4)
    private int basicSalary;
    @ExcelColumn(headerName = "공제총액", sort=5)
    private int deduction;
    @ExcelColumn(headerName = "실지급액", sort=6)
    private int netSalary;
}
