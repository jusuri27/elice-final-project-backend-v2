package com.hr_handlers.employee.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdateRequestDto {

    @NotBlank(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotBlank(message = "연락처 입력은 필수입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String phone;

    private String introduction;

    private String profileImageUrl;
}