package com.hr_handlers.admin.dto.salary.request;

import com.hr_handlers.salary.entity.Salary;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Optional;

@Getter
public class AdminSalaryUpdateRequestDto {

    // @NotNull로 유효성 검사 적용위해 (int -> integer)로 데이터 타입 설정
    // 수정시 사원 정보관련 내용은 수정 안하도록 설정
      // 만약 사원 정보관련 수정이 필요하다면 해당 row를 삭제하고 추가하는게 맞다고 생각

    @NotNull(message = "급여관리 id가 없습니다.")
    private Long salaryId;

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


    public Salary toUpdateEntity(Salary entity) {
        Optional.ofNullable(this.getBasicSalary()).ifPresent(entity::setBasicSalary);
        Optional.ofNullable(this.getDeduction()).ifPresent(entity::setDeduction);
        Optional.ofNullable(this.getNetSalary()).ifPresent(entity::setNetSalary);
        Optional.of(this.getPayDate()).ifPresent(entity::setPayDate);
        Optional.of(String.valueOf(this.payDate.getYear())).ifPresent(entity::setYear);
        return entity;
    }
}
