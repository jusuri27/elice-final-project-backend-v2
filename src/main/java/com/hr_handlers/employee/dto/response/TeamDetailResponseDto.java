package com.hr_handlers.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TeamDetailResponseDto {
    private String profileImageUrl;
    private String position;
    private String name;
}