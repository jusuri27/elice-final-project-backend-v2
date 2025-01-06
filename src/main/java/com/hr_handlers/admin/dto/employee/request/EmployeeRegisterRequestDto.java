package com.hr_handlers.admin.dto.employee.request;

import com.hr_handlers.employee.enums.ContractType;
import com.hr_handlers.employee.enums.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRegisterRequestDto {

    @NotBlank(message = "사원 번호는 필수입니다.")
    private String empNo;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 50, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotNull(message = "휴가 잔여일수는 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = true, message = "휴가 잔여일수는 0 이상이어야 합니다.")
    @DecimalMax(value = "20.0", inclusive = true, message = "휴가 잔여일수는 20 이하이어야 합니다.")
    private Double leaveBalance;

    @NotNull(message = "권한은 필수입니다.")
    private Role role;

    @NotNull(message = "계약 형태는 필수입니다.")
    private ContractType contractType;

    @NotNull(message = "입사일은 필수입니다.")
    private LocalDate joinDate;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "직급은 필수입니다.")
    private String position;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "연락처는 필수입니다.")
    @Size(min = 11, max = 11, message = "연락처는 '-' 없이 11자리이어야 합니다.")
    private String phone;

    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthDate;

    @NotBlank(message = "부서명은 필수입니다.")
    private String deptName;
}