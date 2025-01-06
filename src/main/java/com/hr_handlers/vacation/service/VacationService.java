package com.hr_handlers.vacation.service;

import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.employee.repository.EmployeeRepository;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import com.hr_handlers.vacation.dto.*;
import com.hr_handlers.vacation.entity.Vacation;
import com.hr_handlers.vacation.entity.VacationStatus;
import com.hr_handlers.vacation.mapper.VacationMapper;
import com.hr_handlers.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;
    private final VacationMapper vacationMapper;
    private final VacationHandler vacationHandler;

    private final EmployeeRepository empRepository;

    //휴가 상세 조회
    public SuccessResponse<VacationDetailResponseDto> getVacationDetail(Long id){

        return SuccessResponse.of(
                "휴가 상세 조회 성공",
                vacationRepository.findVacationDetailById(id));
    }

    // 휴가 승인 대기 목록 조회
    public SuccessResponse<List<PendingVacationResponseDto>> getPendingVacations(String empNo){

        return SuccessResponse.of(
                "승인 대기 휴가 목록 조회 성공",
                vacationRepository.findPendingVacations(empNo));
    }

    // 휴가 승인 확정 목록 조회
    public SuccessResponse<Page<ApprovedVacationResponseDto>> getApprovedVacations(String empNo, Pageable pageable) {
        return SuccessResponse.of(
                "승인 확정 휴가 목록 조회 성공",
                vacationRepository.findApprovedVacations(empNo, pageable));
    }

    // 휴가 등록
    public SuccessResponse<VacationResponseDto> enrollVacation(VacationRequestDto request, String empNo){
        Employee employee = empRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // 휴가 일수 계산 및 검증
        vacationHandler.validateVacationDays(
                employee,
                request.getType(),
                request.getStartDate(),
                request.getEndDate()
        );

        // 휴가 일수 차감
        vacationHandler.processVacationDays(
                employee,
                request.getType(),
                request.getStartDate(),
                request.getEndDate(),
                false
        );

        Vacation vacation = Vacation.builder()
                .docNum(generateDocNum())
                .title(request.getTitle())
                .type(request.getType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .status(VacationStatus.PENDING)
                .employee(employee)
                .build();

        vacationRepository.save(vacation);

        return SuccessResponse.of(
                "휴가 등록 성공",
                vacationMapper.toVacationResponse(vacation));
    }

    // 휴가 수정
    @Transactional
    public SuccessResponse<VacationResponseDto> modifyVacation(Long id, VacationRequestDto request){
        Vacation vacation = vacationRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.VACATION_NOT_FOUND));

        // 기존 휴가 일수 복구
        vacationHandler.processVacationDays(
                vacation.getEmployee(),
                vacation.getType(),
                vacation.getStartDate(),
                vacation.getEndDate(),
                true
        );

        // 새로운 휴가 일수 검증
        vacationHandler.validateVacationDays(
                vacation.getEmployee(),
                request.getType(),
                request.getStartDate(),
                request.getEndDate()
        );

        // 새로운 휴가 일수 차감
        vacationHandler.processVacationDays(
                vacation.getEmployee(),
                request.getType(),
                request.getStartDate(),
                request.getEndDate(),
                false
        );

        vacation.modify(request);

        return SuccessResponse.of(
                "휴가 수정 성공",
                vacationMapper.toVacationResponse(vacation));
    }

    // 휴가 삭제
    @Transactional
    public SuccessResponse<Boolean> deleteVacation(Long id){
        Vacation vacation = vacationRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.VACATION_NOT_FOUND));

        vacationRepository.delete(vacation);

        // 기존 휴가 일수 복구
        vacationHandler.processVacationDays(
                vacation.getEmployee(),
                vacation.getType(),
                vacation.getStartDate(),
                vacation.getEndDate(),
                true
        );

        return SuccessResponse.of(
                "휴가 삭제 성공",
                Boolean.TRUE);
    }

    // 휴가 일수 정보 조회
    public SuccessResponse<VacationSummaryResponseDto> getBalance(String empNo){
        VacationSummaryResponseDto response = vacationRepository.findEmployeeVacationBalanceById(empNo);

        return  SuccessResponse.of(
                "휴가 일수 정보 조회 성공",
                response);
    }



    // 문서 번호 생성 로직
    private String generateDocNum() {
        String year = String.valueOf(LocalDate.now().getYear()); // 현재 연도 가져오기
        String randomPart = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8); // 랜덤 문자열 8자리

        return year + randomPart;
    }
}
