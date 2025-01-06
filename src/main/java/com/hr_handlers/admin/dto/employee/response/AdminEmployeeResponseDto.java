package com.hr_handlers.admin.dto.employee.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class AdminEmployeeResponseDto {
    private String empNo;
    private String name;
    private String email;
    private String phone;
    private String position;
    private LocalDate birthDate;
    private String contractType;
    private LocalDate joinDate;
    private String introduction;
    private Double leaveBalance;
    private String profileImageUrl;
    private String deptName;
}
