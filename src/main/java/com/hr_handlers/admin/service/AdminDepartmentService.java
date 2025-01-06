package com.hr_handlers.admin.service;

import com.hr_handlers.admin.dto.employee.response.AdminDepartmentResponseDto;
import com.hr_handlers.admin.repository.employee.AdminDepartmentRepository;
import com.hr_handlers.employee.entity.Department;
import com.hr_handlers.employee.mapper.EmployeeMapper;
import com.hr_handlers.global.dto.SearchRequestDto;
import com.hr_handlers.global.dto.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class AdminDepartmentService {

    private final AdminDepartmentRepository deptRepository;

    // 부서 등록
    public SuccessResponse<String> createDepartment(@RequestParam String deptName) {
        if (deptRepository.existsByDeptName(deptName)) {
            throw new IllegalArgumentException("이미 존재하는 부서명입니다.");
        }

        Department department = Department.builder()
                .deptName(deptName)
                .build();
        deptRepository.save(department);

        return SuccessResponse.of("부서 등록 성공", "부서가 등록되었습니다.");
    }

    // 부서 전체 조회
    public SuccessResponse<Page<AdminDepartmentResponseDto>> getAllDept(SearchRequestDto requestDto) {
        Page<Department> departments = deptRepository.findDeptByName(requestDto);

        return SuccessResponse.of("부서 조회 성공", departments.map(EmployeeMapper::toDeptListResponseDto));
    }

    // 부서 수정
    public SuccessResponse<Boolean> updateDepartment(Long id, String deptName) {
        deptRepository.updateDept(id, deptName);
        return SuccessResponse.of("부서 수정 성공", true);
    }

    // 부서 삭제
    public SuccessResponse<Boolean> delete(Long id) {
        deptRepository.deleteDept(id);
        return SuccessResponse.of("부서 삭제 성공", true);
    }
}