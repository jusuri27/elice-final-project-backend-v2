package com.hr_handlers.employee.mapper;

import com.hr_handlers.admin.dto.employee.request.EmployeeRegisterRequestDto;
import com.hr_handlers.admin.dto.employee.response.AdminDepartmentResponseDto;
import com.hr_handlers.admin.dto.employee.response.AdminEmployeeResponseDto;
import com.hr_handlers.employee.dto.response.EmpDetailResponseDto;
import com.hr_handlers.employee.entity.Department;
import com.hr_handlers.employee.entity.Employee;

public class EmployeeMapper {

    // EmpRegisterDto -> Employee
    public static Employee toEmployeeEntity(EmployeeRegisterRequestDto registerRequest, String encodedPassword, Department department) {
        return Employee.builder()
                .empNo(registerRequest.getEmpNo())
                .password(encodedPassword) // 암호화된 비밀번호
                .leaveBalance(registerRequest.getLeaveBalance())
                .role(registerRequest.getRole())
                .contractType(registerRequest.getContractType())
                .joinDate(registerRequest.getJoinDate())
                .name(registerRequest.getName())
                .position(registerRequest.getPosition())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .birthDate(registerRequest.getBirthDate())
                .department(department)
                .build();
    }

    // Employee -> EmpDetailResponseDto
    public static EmpDetailResponseDto toEmpDetailResponseDto(Employee employee) {
        return EmpDetailResponseDto.builder()
                .empNo(employee.getEmpNo())
                .email(employee.getEmail())
                .name(employee.getName())
                .position(employee.getPosition())
                .phone(employee.getPhone())
                .role(String.valueOf(employee.getRole()))
                .birthDate(employee.getBirthDate())
                .deptName(employee.getDepartment() != null ? employee.getDepartment().getDeptName() : null)
                .contractType(String.valueOf(employee.getContractType()))
                .leaveBalance(employee.getLeaveBalance())
                .joinDate(employee.getJoinDate())
                .introduction(employee.getIntroduction())
                .profileImage(employee.getProfileImage() != null ? employee.getProfileImage().getProfileImageUrl() : null)
                .build();
    }

    // Employee -> EmpListResponseDto
    public static AdminEmployeeResponseDto toEmpListResponseDto(Employee employee) {
        return AdminEmployeeResponseDto.builder()
                .empNo(employee.getEmpNo())
                .name(employee.getName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .position(employee.getPosition())
                .birthDate(employee.getBirthDate())
                .contractType(employee.getContractType() != null ? employee.getContractType().getDescription() : "알 수 없음")
                .joinDate(employee.getJoinDate())
                .introduction(employee.getIntroduction())
                .leaveBalance(employee.getLeaveBalance())
                .profileImageUrl(employee.getProfileImage() != null ? employee.getProfileImage().getProfileImageUrl() : null)
                .deptName(employee.getDepartment() != null ? employee.getDepartment().getDeptName() : null)
                .build();
    }

    // Department -> AdminDeptResponseDto
    public static AdminDepartmentResponseDto toDeptListResponseDto(Department department){
        return AdminDepartmentResponseDto.builder()
                .id(department.getId())
                .deptName(department.getDeptName())
                .build();
    }
}