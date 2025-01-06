package com.hr_handlers.admin.dto.salary.request;

import lombok.Getter;

@Getter
public class AdminSalaryExcelRequestDto {
    private AdminSalarySearchRequestDto searchParam;
    private AdminSalaryExcelTypeRequestDto excelTypeParam;
}
