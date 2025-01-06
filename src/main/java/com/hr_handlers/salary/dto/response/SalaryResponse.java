package com.hr_handlers.salary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SalaryResponse {

    private Long salayId;
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
