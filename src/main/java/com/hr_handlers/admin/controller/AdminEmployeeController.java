package com.hr_handlers.admin.controller;

import com.hr_handlers.admin.dto.employee.request.EmployeeRegisterRequestDto;
import com.hr_handlers.admin.dto.employee.request.AdminEmployeeUpdateRequestDto;
import com.hr_handlers.global.dto.SearchRequestDto;
import com.hr_handlers.admin.dto.employee.response.AdminEmployeeResponseDto;
import com.hr_handlers.admin.service.AdminEmployeeService;
import com.hr_handlers.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/emp")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "사원 관리", description = "사원 관리 API")
public class AdminEmployeeController {

    private final AdminEmployeeService adminEmployeeService;

    @PostMapping
    @Operation(summary = "사원 등록", description = "사원을 등록합니다.")
    public SuccessResponse<String> registerEmp(@Valid @RequestBody EmployeeRegisterRequestDto registerDto){
        return adminEmployeeService.register(registerDto);
    }

    @GetMapping
    @Operation(summary = "사원 전체 조회", description = "모든 사원을 조회, 검색합니다.")
    public SuccessResponse<Page<AdminEmployeeResponseDto>> getAllEmp(
            @RequestParam(defaultValue = "0", value = "page") int page,
            @RequestParam(defaultValue = "10", value = "size") int size,
            @RequestParam(defaultValue = "createdAt", value = "sortField") String sortField,
            @RequestParam(defaultValue = "desc", value = "sortDir") String sortDir,
            @RequestParam(required = false, value = "keyword") String keyword) {
        SearchRequestDto requestDto = new SearchRequestDto(page, size, sortField, sortDir, keyword);

        return adminEmployeeService.getAllEmp(requestDto);
    }

    @PatchMapping("/{empNo}")
    @Operation(summary = "사원 정보 수정", description = "사원의 직급, 부서, 계약형태를 수정합니다.")
    public SuccessResponse<Boolean> updateEmpDetail(
            @PathVariable("empNo") String empNo,
            @Valid @RequestBody AdminEmployeeUpdateRequestDto updateRequest){
        return adminEmployeeService.updateEmpDetail(empNo, updateRequest);
    }

    @DeleteMapping("/{empNo}")
    @Operation(summary = "사원 삭제", description = "사원을 삭제합니다.")
    public SuccessResponse<Boolean> deleteEmp(@PathVariable("empNo") String empNo){
        return adminEmployeeService.delete(empNo);
    }

    @GetMapping("/search")
    @Operation(summary = "사원 검색", description = "직급. 부서로 사원을 검색합니다.")
    public SuccessResponse<List<AdminEmployeeResponseDto>> searchEmp(
            @RequestParam("position") String position,
            @RequestParam("deptName") String deptName) {
        return adminEmployeeService.searchEmp(position, deptName);
    }
}