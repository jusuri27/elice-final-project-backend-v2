package com.hr_handlers.admin.dto.salary.request;

import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.salary.entity.Salary;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AdminSalaryCreateRequestDto {

    // @NotNull로 유효성 검사 적용위해 (int -> integer)로 데이터 타입 설정


    @NotNull(message = "사원 id가 없습니다.")
    private String employeeId;

    @NotNull(message = "기본급이 없습니다.")
    @Positive(message = "기본급은 0보다 커야 합니다.")
    private Integer basicSalary;

    @NotNull(message = "공제 금액이 없습니다.")
    @Positive(message = "공제 금액은 0보다 커야 합니다.")
    private Integer deduction;

    @NotNull(message = "실수령액이 없습니다.")
    @Positive(message = "실수령액은 0보다 커야 합니다.")
    private Integer netSalary;

    @NotNull(message = "급여지급일이 없습니다.")
    private LocalDate payDate;

    public Salary toCreateEntity(Employee employee) {
        return Salary.builder()
                .employee(employee)
                .basicSalary(this.basicSalary)
                .deduction(this.deduction)
                .netSalary(this.netSalary)
                .payDate(this.payDate)
                .year(String.valueOf(this.payDate.getYear()))
                .build();
    }
}
