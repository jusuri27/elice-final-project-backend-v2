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
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSalaryService {

    private final AdminSalaryRepository adminSalaryRepository;
    private final EmployeeRepository empRepository;
    private final ExcelUploadUtils excelUploadUtils;

    private final AdminSalaryMapper adminSalaryMapper;
    private final SqlSessionFactory sqlSessionFactory;
    
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

    // 급여관리 엑셀 업로드 -> saveAll()
    @Transactional
    public SuccessResponse<Boolean> excelUploadSalary(List<AdminSalaryExcelUploadRequestDto> adminSalaryExcelUploadRequestDtos) {

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

//        adminSalaryRepository.saveAll(salaries);
//        foreachInsert(salaries);
        sqlSessionInsert(salaries);

        return SuccessResponse.of("급여가 등록 되었습니다.", true);
    }

    // mybatis foreach
    public void foreachInsert(List<Salary> salaries) {

        // 배치 단위 설정 (10만 건)
        final int BATCH_SIZE = 100_000;
        int totalSize = salaries.size();

        for (int i = 0; i < totalSize; i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, totalSize);
            List<Salary> batch = salaries.subList(i, endIndex);

            adminSalaryMapper.excelUploadWithForeach(batch);

            // 메모리 상태 출력
            printMemoryUsage(i / BATCH_SIZE + 1);
        }

    }

    // mybatis sqlSession
    public void sqlSessionInsert(List<Salary> salaries) {
        final int BATCH_SIZE = 10_000;
        int totalSize = salaries.size();

        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);
        AdminSalaryMapper mapper = session.getMapper(AdminSalaryMapper.class);

        for (int i = 0; i < totalSize; i++) {
            mapper.excelUploadWithSqlSession(salaries.get(i));

            if(i % BATCH_SIZE == 0) {
                session.flushStatements(); // 쌓인 쿼리 실행
                session.clearCache(); // 1차 캐쉬 제거
                printMemoryUsage(i / BATCH_SIZE + 1);
            }
        }
        session.commit();
    }

    private void printMemoryUsage(int batchNumber) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();   // JVM 전체 메모리
        long freeMemory = runtime.freeMemory();     // 사용 가능한 메모리
        long usedMemory = totalMemory - freeMemory; // 사용 중인 메모리

        System.out.printf("수행횟수 %d\tTotal Memory: %.3f MB\tFree Memory: %.3f MB\tUsed Memory: %.3f MB%n",
                batchNumber,
                bytesToMB(totalMemory),
                bytesToMB(freeMemory),
                bytesToMB(usedMemory));

    }

    private double bytesToMB(long bytes) {
        return bytes / 1024.0 / 1024.0;
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
