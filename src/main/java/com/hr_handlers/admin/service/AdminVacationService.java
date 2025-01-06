package com.hr_handlers.admin.service;

import com.hr_handlers.admin.dto.vacation.AdminVacationResponseDto;
import com.hr_handlers.admin.dto.vacation.AdminVacationStatusResponseDto;
import com.hr_handlers.admin.repository.vacation.AdminVacationRepository;
import com.hr_handlers.employee.repository.EmployeeRepository;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import com.hr_handlers.vacation.dto.VacationResponseDto;
import com.hr_handlers.vacation.entity.Vacation;
import com.hr_handlers.vacation.entity.VacationType;
import com.hr_handlers.vacation.mapper.VacationMapper;
import com.hr_handlers.vacation.service.VacationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminVacationService {

    private final AdminVacationRepository adminVacationRepository;
    private final VacationHandler vacationHandler;
    private final EmployeeRepository empRepository;

    private final VacationMapper vacationMapper;

    // 승인 대기 휴가 목록 페이징 조회
    public SuccessResponse<Page<AdminVacationResponseDto>> getPendingVacations(Pageable pageable) {
        return SuccessResponse.of(
                "승인 대기 휴가 목록 조회 성공",
                adminVacationRepository.findPendingVacations(pageable)
        );
    }

    // 휴가 보유 현황 페이징 조회
    public SuccessResponse<Page<AdminVacationStatusResponseDto>> getVacationStatus(Pageable pageable) {
        return SuccessResponse.of(
                "휴가 보유 현황 조회 성공",
                adminVacationRepository.findVacationStatusForAllEmployees(pageable)
        );
    }

    // 휴가 승인
    @Transactional
    public SuccessResponse<VacationResponseDto> approveVacation(Long id, String empNo){
        Vacation vacation = adminVacationRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.VACATION_NOT_FOUND));

        // 결재자
        String approverName = empRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND)).getName();

        // 승인 처리
        vacation.approve(approverName);

        return SuccessResponse.of(
                "휴가 승인 처리 성공",
                vacationMapper.toVacationResponse(vacation)
        );
    }

    // 휴가 반려
    @Transactional
    public SuccessResponse<VacationResponseDto> rejectVacation(Long id, String empNo){
        Vacation vacation = adminVacationRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.VACATION_NOT_FOUND));

        //결재자
        String approverName = empRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND)).getName();

        // 반려 처리
        vacation.reject(approverName);

        // 차감됐던 휴가 일수 복구
        if (vacation.getType() != VacationType.PUBLIC) {
            double vacationDays = vacationHandler.calculateVacationDays(
                    vacation.getType(),
                    vacation.getStartDate(),
                    vacation.getEndDate()
            );
            vacation.getEmployee().leaveBalanceUpdate(-vacationDays); // 차감했던 일수 복구
        }

        return SuccessResponse.of(
                "휴가 반려 처리 성공",
                vacationMapper.toVacationResponse(vacation)
        );
    }
}
