package com.hr_handlers.admin.dto.salary.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AdminSalaryResponseDto {

    private Long salaryId;
    private Long employeeId;
    private String deptName;
    private String position;
    private String name;
    private int basicSalary;
    private int deduction;
    private int netSalary;
    private LocalDate payDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
