package com.hr_handlers.employee.controller;

import com.hr_handlers.employee.dto.request.EmployeeUpdateRequestDto;
import com.hr_handlers.employee.dto.request.PasswordRecoveryRequestDto;
import com.hr_handlers.employee.dto.request.PasswordUpdateRequestDto;
import com.hr_handlers.employee.dto.response.EmpDetailResponseDto;
import com.hr_handlers.employee.dto.response.TeamDetailResponseDto;
import com.hr_handlers.employee.service.EmployeeService;

import com.hr_handlers.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/emp")
@PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
@Tag(name = "사원", description = "사원 API")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "시원 조회", description = "시원이 마이페이지를 조회합니다.")
    public SuccessResponse<EmpDetailResponseDto> getEmpDetail(Authentication authentication){
        return employeeService.getEmpDetail(authentication.getName());
    }

    @PatchMapping
    @Operation(summary = "시원 수정", description = "시원이 마이페이지를 수정합니다.")
    public SuccessResponse<Boolean> updateEmpDetail(
            Authentication authentication,
            @RequestPart("updateRequest") EmployeeUpdateRequestDto updateRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImageFile) throws IOException {
        return employeeService.updateEmpDetail(authentication.getName(), updateRequest, profileImageFile);
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 변경", description = "사원이 비밀번호를 변경합니다.")
    public SuccessResponse<Boolean> updatePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordUpdateRequestDto requestDto){
        return employeeService.updatePassword(authentication.getName(), requestDto);
    }

    @PostMapping("/check")
    @Operation(summary = "비밀번호 찾기", description = "이메일과 사원 번호가 일치하는지 검사합니다.")
    public SuccessResponse<Boolean> matchEmailAndEmpNo(@Valid @RequestBody PasswordRecoveryRequestDto requestDto){
        return employeeService.matchEmailAndEmpNo(requestDto.getEmpNo(), requestDto.getEmail());
    }
    // TODO: api 병합
    @PostMapping("/send/mail")
    @Operation(summary = "메일 전송", description = "임시 비밀번호를 메일로 전송합니다.")
    public SuccessResponse<Boolean> sendResetPassword(@Valid @RequestBody PasswordRecoveryRequestDto requestDto){
        return employeeService.sendMail(employeeService.sendResetPassword(requestDto.getEmpNo(), requestDto.getEmail()));
    }

    @GetMapping("/team")
    @Operation(summary = "같은 부서의 사원 조회", description = "같은 부서 사원들의 정보를 조회합니다.")
    public SuccessResponse<List<TeamDetailResponseDto>> getTeamDetail(Authentication authentication){
        return employeeService.getTeamDetail(authentication.getName());
    }
}