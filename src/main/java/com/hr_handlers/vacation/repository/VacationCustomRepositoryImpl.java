package com.hr_handlers.vacation.repository;

import com.hr_handlers.vacation.dto.ApprovedVacationResponseDto;
import com.hr_handlers.vacation.dto.PendingVacationResponseDto;
import com.hr_handlers.vacation.dto.VacationDetailResponseDto;
import com.hr_handlers.vacation.dto.VacationSummaryResponseDto;
import com.hr_handlers.vacation.entity.VacationStatus;
import com.hr_handlers.vacation.entity.VacationType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hr_handlers.employee.entity.QEmployee.employee;
import static com.hr_handlers.vacation.entity.QVacation.vacation;

@Repository
@Slf4j
public class VacationCustomRepositoryImpl implements VacationCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public VacationCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory){
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public VacationDetailResponseDto findVacationDetailById(Long id) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                VacationDetailResponseDto.class,
                                vacation.id,
                                vacation.docNum,
                                vacation.title,
                                vacation.type,
                                vacation.startDate,
                                vacation.endDate,
                                vacation.reason
                        )
                )
                .from(vacation)
                .where(
                        vacation.id.eq(id)
                )
                .fetchOne();
    }

    @Override
    public List<PendingVacationResponseDto> findPendingVacations(String empNo) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                PendingVacationResponseDto.class,
                                vacation.id,
                                vacation.docNum,
                                vacation.title,
                                vacation.type,
                                vacation.updatedAt,
                                vacation.startDate,
                                vacation.endDate,
                                vacation.employee.id
                        )
                )
                .from(vacation)
                .where(
                        vacation.employee.empNo.eq(empNo)
                            .and(vacation.status.eq(VacationStatus.PENDING))
                )
                .fetch();
    }

    @Override
    public Page<ApprovedVacationResponseDto> findApprovedVacations(String empNo, Pageable pageable) {
        List<ApprovedVacationResponseDto> content = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ApprovedVacationResponseDto.class,
                                vacation.docNum,
                                vacation.title,
                                vacation.updatedAt,
                                vacation.startDate,
                                vacation.endDate,
                                vacation.approvedAt,
                                vacation.status,
                                vacation.approver,
                                vacation.employee.id
                        )
                )
                .from(vacation)
                .where(
                        vacation.employee.empNo.eq(empNo)
                                .and(vacation.status.eq(VacationStatus.APPROVED)
                                        .or(vacation.status.eq(VacationStatus.REJECTED)))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(vacation.count())
                .from(vacation)
                .where(
                        vacation.employee.empNo.eq(empNo)
                                .and(vacation.status.eq(VacationStatus.APPROVED)
                                        .or(vacation.status.eq(VacationStatus.REJECTED)))
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }


    // 휴가 정보 조회 - 휴가 잔여 일수, 승인 확정 휴가, 승인 대기 휴가
    @Override
    public VacationSummaryResponseDto findEmployeeVacationBalanceById(String empNo) {
        VacationStatus approvedStatus = VacationStatus.APPROVED;
        VacationStatus pendingStatus = VacationStatus.PENDING;


        // 연차 계산 - 시작일과 종료일 간의 차이
        NumberTemplate<Double> dateDiff = Expressions.numberTemplate(
                Double.class,
                "DATEDIFF({0}, {1})",
                vacation.endDate,
                vacation.startDate
        );

        // 휴가 일수 계산. 반차는 0.5일, 공가는 0일, 그 외는 dateDiff로 처리
        NumberExpression<Double> vacationDaysExpression = new CaseBuilder()
                .when(vacation.type.eq(VacationType.HALF))
                .then(Expressions.numberTemplate(Double.class, "0.5"))
                .when(vacation.type.eq(VacationType.PUBLIC))
                .then(Expressions.numberTemplate(Double.class, "0.0"))
                .otherwise(dateDiff.add(1.0));

        // 사번이 empNo인 사원의 확정 휴가 일수 합산
        SubQueryExpression<Double> approvedSum = JPAExpressions
                .select(vacationDaysExpression.sum())
                .from(vacation)
                .where(vacation.employee.empNo.eq(empNo)
                        .and(vacation.status.eq(approvedStatus)));

        // 사번이 empNo인 사원의 승인 대기 휴가 일수 계산
        SubQueryExpression<Double> pendingSum = JPAExpressions
                .select(vacationDaysExpression.sum())
                .from(vacation)
                .where(vacation.employee.empNo.eq(empNo)
                        .and(vacation.status.eq(pendingStatus)));

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                VacationSummaryResponseDto.class,
                                employee.leaveBalance,
                                Expressions.numberTemplate(Double.class, "COALESCE({0}, 0.0)", approvedSum),
                                Expressions.numberTemplate(Double.class, "COALESCE({0}, 0.0)", pendingSum)
                        )
                )
                .from(employee)
                .where(employee.empNo.eq(empNo))
                .fetchOne();
    }
}
