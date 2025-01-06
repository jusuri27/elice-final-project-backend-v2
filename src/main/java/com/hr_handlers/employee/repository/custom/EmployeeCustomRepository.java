package com.hr_handlers.employee.repository.custom;

import com.hr_handlers.employee.dto.request.EmployeeUpdateRequestDto;
import com.hr_handlers.employee.dto.response.TeamDetailResponseDto;

import java.util.List;

public interface EmployeeCustomRepository {
    void updateEmp(String emp, EmployeeUpdateRequestDto requestDto, String newProfileImageUrl);

    List<TeamDetailResponseDto> findTeamMembers(String empNo);
}