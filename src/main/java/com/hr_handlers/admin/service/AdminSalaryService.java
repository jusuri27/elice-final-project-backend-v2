package com.hr_handlers.admin.service;

import com.hr_handlers.admin.dto.salary.request.*;
import com.hr_handlers.admin.dto.salary.request.excel.DepartmentMonthSalaryExcelRequestDto;
import com.hr_handlers.admin.dto.salary.request.excel.DepartmentYearSalaryExcelRequestDto;
import com.hr_handlers.admin.dto.salary.request.excel.IndividualMonthSalaryExcelRequestDto;
import com.hr_handlers.admin.dto.salary.request.excel.IndividualYearSalaryExcelRequestDto;
import com.hr_handlers.admin.dto.salary.response.AdminSalaryResponseDto;
import com.hr_handlers.admin.repository.salary.AdminSalaryRepository;
import com.hr_handlers.admin.mapper.AdminSalaryMapper;
import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.employee.repository.EmployeeRepository;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import com.hr_handlers.global.utils.ExcelUploadUtils;
import com.hr_handlers.salary.entity.Salary;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSalaryService {

    private final AdminSalaryRepository adminSalaryRepository;
    private final EmployeeRepository empRepository;
    private final ExcelUploadUtils excelUploadUtils;

    private final AdminSalaryMapper adminSalaryMapper;
    
    // 급여관리 전체 조회
    public SuccessResponse<List<AdminSalaryResponseDto>> getAllUserSalary() {
        return SuccessResponse.of("급여 관리 조회 성공", adminSalaryRepository.findAllSalary());
    }

    // 급여관리 조건 조회
    public SuccessResponse<Page<AdminSalaryResponseDto>> searchSalary(Pageable pageable, AdminSalarySearchRequestDto adminSalarySearchRequestDto) {
        return SuccessResponse.of("급여 관리 조회 성공", adminSalaryRepository.searchSalaryByFilter(pageable, adminSalarySearchRequestDto));
    }

    // 급여관리 추가
    public SuccessResponse<Boolean> createSalary(AdminSalaryCreateRequestDto salaryCreateRequest) {
        Employee employee = empRepository.findByEmpNo(salaryCreateRequest.getEmployeeId()).orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));
        Salary salaryEntity = salaryCreateRequest.toCreateEntity(employee);
        adminSalaryRepository.save(salaryEntity);
        return SuccessResponse.of("급여가 등록 되었습니다.", true);
    }

    // 급여관리 수정
    @Transactional
    public SuccessResponse<Boolean> updateSalary(AdminSalaryUpdateRequestDto adminSalaryUpdateRequestDto) {
        Salary salaryEntity = adminSalaryRepository.findById(adminSalaryUpdateRequestDto.getSalaryId()).orElseThrow(() -> new GlobalException(ErrorCode.SALARY_NOT_FOUND));
        adminSalaryUpdateRequestDto.toUpdateEntity(salaryEntity);
        return SuccessResponse.of("급여가 수정 되었습니다.", true);
    }

    // 급여관리 삭제
    public SuccessResponse<Boolean> deleteSalary(List<Long> salaryIds) {
        adminSalaryRepository.deleteAllByIdInBatch(salaryIds);
        return SuccessResponse.of("급여가 삭제 되었습니다.", true);
    }

    // 급여관리 엑셀 업로드
    @Transactional
    public SuccessResponse<Boolean> excelUploadSalary(List<AdminSalaryExcelUploadRequestDto> adminSalaryExcelUploadRequestDtos) {

        // todo : 엑셀dto에서 a사원의 2024-11-13 row가 2개 이상일 경우 유효성 검사 어떻게 처리할지??
        // todo : 엑셀에 빈 행이 있을경우 유효성 검사??

        // 엑셀 dto에서 Employee ID 목록 뽑아내기
        List<String> employeeIds = adminSalaryExcelUploadRequestDtos.stream()
                .map(AdminSalaryExcelUploadRequestDto::getEmployeeId)
                .distinct()  // 중복되는 Employee ID를 제외
                .collect(Collectors.toList());

        // Employee ID 목록을 받아서 한 번에 Employee 객체들을 조회
        Map<String, Employee> employeeMap = empRepository.findAllByEmpNoIn(employeeIds).stream()
                .collect(Collectors.toMap(Employee::getEmpNo, e -> e));

        List<Salary> salaries = adminSalaryExcelUploadRequestDtos.stream()
                .map(request -> {
                    Employee employee = Optional.ofNullable(employeeMap.get(request.getEmployeeId()))
                            .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));
                    return request.toCreateEntity(employee);
                })
                .collect(Collectors.toList());

        adminSalaryRepository.saveAll(salaries);

        return SuccessResponse.of("급여가 등록 되었습니다.", true);
    }

    public SuccessResponse<Boolean> excelDownloadSalary(OutputStream stream, AdminSalaryExcelRequestDto adminSalaryExcelRequestDto) throws IOException, IllegalAccessException {

        // 현재 excelUtil로는 응답값과 헤더의 순서가 일치해야 정상작동함
        // 그래서 집계 방식마다 excelDto 만드는중....
        // 리팩토링이 필요...
        List<T> adminSalaryResponseDtos = getSalarySummaryByType(adminSalaryExcelRequestDto);
        Class<T> adminSalaryResponseClazz = getDtoClassForResponseType(adminSalaryExcelRequestDto);
        excelUploadUtils.renderObjectToExcel(stream, adminSalaryResponseDtos, adminSalaryResponseClazz);
        return SuccessResponse.of("성공적으로 다운로드 되었습니다.", true);
    }

    private List<T> getSalarySummaryByType(AdminSalaryExcelRequestDto adminSalaryExcelRequestDto) {
        String downloadScope = adminSalaryExcelRequestDto.getExcelTypeParam().getDownloadScope();
        String timePeriod = adminSalaryExcelRequestDto.getExcelTypeParam().getTimePeriod();

        if ("individual".equals(downloadScope)) {
            if ("yearly".equals(timePeriod)) {
                return adminSalaryMapper.getYearlySummaryByIndividual(adminSalaryExcelRequestDto.getSearchParam());
            } else if ("monthly".equals(timePeriod)) {
                return adminSalaryMapper.getMonthlySummaryByIndividual(adminSalaryExcelRequestDto.getSearchParam());
            } else {
                throw new GlobalException(ErrorCode.INVALID_TIME_PERIOD);
            }
        } else if ("department".equals(downloadScope)) {
            if ("yearly".equals(timePeriod)) {
                return adminSalaryMapper.getYearlySummaryByDepartment(adminSalaryExcelRequestDto.getSearchParam());
            } else if ("monthly".equals(timePeriod)) {
                return adminSalaryMapper.getMonthlySummaryByDepartment(adminSalaryExcelRequestDto.getSearchParam());
            } else {
                throw new GlobalException(ErrorCode.INVALID_TIME_PERIOD);
            }
        } else {
            throw new GlobalException(ErrorCode.INVALID_DOWNLOAD_SCOPE);
        }
    }

    // DTO에 맞는 클래스를 반환하는 헬퍼 메서드
    private <T> Class<T> getDtoClassForResponseType(AdminSalaryExcelRequestDto adminSalaryExcelRequestDto) {
        String downloadScope = adminSalaryExcelRequestDto.getExcelTypeParam().getDownloadScope();
        String timePeriod = adminSalaryExcelRequestDto.getExcelTypeParam().getTimePeriod();
        if ("individual".equals(downloadScope)) {
            if ("yearly".equals(timePeriod)) {
                return (Class<T>) IndividualYearSalaryExcelRequestDto.class;
            } else if ("monthly".equals(timePeriod)) {
                return (Class<T>) IndividualMonthSalaryExcelRequestDto.class;
            } else {
                throw new GlobalException(ErrorCode.INVALID_TIME_PERIOD);
            }
        } else if ("department".equals(downloadScope)) {
            if ("yearly".equals(timePeriod)) {
                return (Class<T>) DepartmentYearSalaryExcelRequestDto.class;
            } else if ("monthly".equals(timePeriod)) {
                return (Class<T>) DepartmentMonthSalaryExcelRequestDto.class;
            } else {
                throw new GlobalException(ErrorCode.INVALID_TIME_PERIOD);
            }
        } else {
            throw new GlobalException(ErrorCode.INVALID_DOWNLOAD_SCOPE);
        }
    }
}
