package com.hr_handlers.admin.dto.employee.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminEmployeeUpdateRequestDto {

    @NotBlank(message = "직급은 필수입니다.")
    private String position;

    private String contractType;

    @DecimalMin(value = "0.0", message = "휴가 잔여일수는 0 이상이어야 합니다.")
    @DecimalMax(value = "20.0", message = "휴가 잔여일수는 20 이하이어야 합니다.")
    private Double leaveBalance;

    private String deptName;
}