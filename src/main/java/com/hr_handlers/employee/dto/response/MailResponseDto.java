package com.hr_handlers.employee.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MailResponseDto {
    @NotBlank
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String address;

    @NotBlank
    private String title;

    @NotBlank
    private String message;
}