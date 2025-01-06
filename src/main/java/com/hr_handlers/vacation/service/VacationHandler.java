package com.hr_handlers.vacation.service;

import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import com.hr_handlers.vacation.dto.VacationRequestDto;
import com.hr_handlers.vacation.entity.VacationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class VacationHandler {

    // 휴가 일수 계산 로직
    public double calculateVacationDays(
            VacationType type,
            Timestamp startDate,
            Timestamp endDate
    ) {
        // 고정 일수가 있는 타입(반차, 공가)인 경우
        if (type.getFixedDays() != null) {
            return type.getFixedDays();
        }

        // 연차, 병가의 경우 시작일과 종료일의 차이 계산
        long diff = endDate.getTime() - startDate.getTime();
        // 시작일도 포함해야 하므로 +1
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
    }

    // 휴가 일수 처리 메서드
    public void processVacationDays(
            Employee employee,
            VacationType type,
            Timestamp startDate,
            Timestamp endDate,
            boolean isRestore
    ) {
        if (type != VacationType.PUBLIC) {
            double days = calculateVacationDays(type, startDate, endDate);

            // 복구면 음수(더하기), 차감이면 양수(빼기)
            employee.leaveBalanceUpdate(isRestore ? -days : days);
        }
    }

    // 새로운 휴가 일수 유효성 검사
    public void validateVacationDays(
            Employee employee,
            VacationType type,
            Timestamp startDate,
            Timestamp endDate
    ) {
        if (type != VacationType.PUBLIC) {
            double newDays = calculateVacationDays(type, startDate, endDate);
            if (employee.getLeaveBalance() < newDays) {
                throw new GlobalException(ErrorCode.INSUFFICIENT_LEAVE_BALANCE);
            }
        }
    }

}
