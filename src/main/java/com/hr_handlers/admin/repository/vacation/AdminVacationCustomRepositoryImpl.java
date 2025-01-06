package com.hr_handlers.admin.repository.vacation;


import com.hr_handlers.admin.dto.vacation.AdminVacationResponseDto;
import com.hr_handlers.admin.dto.vacation.AdminVacationStatusResponseDto;
import com.hr_handlers.vacation.entity.VacationStatus;
import com.hr_handlers.vacation.entity.VacationType;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hr_handlers.employee.entity.QDepartment.department;
import static com.hr_handlers.employee.entity.QEmployee.employee;
import static com.hr_handlers.vacation.entity.QVacation.vacation;

@Repository
public class AdminVacationCustomRepositoryImpl implements AdminVacationCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public AdminVacationCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory){
        this.jpaQueryFactory = jpaQueryFactory;
    }

    // 승인 대기 휴가 목록 조회 - 전직원 (관리자 휴가 페이지에서 승인 / 반려 처리 시 사용)
    @Override
    public Page<AdminVacationResponseDto> findPendingVacations(Pageable pageable) {
        List<AdminVacationResponseDto> content = jpaQueryFactory
                .select(Projections.constructor(
                        AdminVacationResponseDto.class,
                        vacation.id,
                        employee.position,
                        employee.department.deptName,
                        employee.name,
                        Expressions.stringTemplate(
                                "CONCAT(DATE_FORMAT({0}, '%Y-%m-%d'), ' ~ ', DATE_FORMAT({1}, '%Y-%m-%d'))",
                                vacation.startDate,
                                vacation.endDate
                        ),
                        vacation.type,
                        new CaseBuilder()
                                .when(vacation.type.eq(VacationType.HALF))
                                .then(Expressions.numberTemplate(Double.class, "0.5"))
                                .when(vacation.type.eq(VacationType.PUBLIC))
                                .then(Expressions.numberTemplate(Double.class, "0.0"))
                                .otherwise(Expressions.numberTemplate(
                                        Double.class,
                                        "DATEDIFF({1}, {0}) + 1",
                                        vacation.startDate,
                                        vacation.endDate
                                )),
                        vacation.status
                ))
                .from(vacation)
                .join(vacation.employee, employee)
                .join(employee.department)
                .where(vacation.status.eq(VacationStatus.PENDING))
                .orderBy(vacation.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(vacation.count())
                .from(vacation)
                .where(vacation.status.eq(VacationStatus.PENDING))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    // 전 직원 연차, 반차, 병가, 공가, 총 사용 휴가, 잔여 휴가 조회
    @Override
    public Page<AdminVacationStatusResponseDto> findVacationStatusForAllEmployees(Pageable pageable) {
        List<AdminVacationStatusResponseDto> content = jpaQueryFactory
                .select(Projections.constructor(AdminVacationStatusResponseDto.class,
                        employee.position,
                        department.deptName.as("deptName"),
                        employee.name,
                        ExpressionUtils.as(calculateVacationDays(VacationType.ANNUAL), "annualLeave"),
                        ExpressionUtils.as(calculateVacationDays(VacationType.HALF), "halfLeave"),
                        ExpressionUtils.as(calculateVacationDays(VacationType.SICK), "sickLeave"),
                        ExpressionUtils.as(calculateVacationDays(VacationType.PUBLIC), "publicLeave"),
                        ExpressionUtils.as(calculateTotalUsedDays(), "totalUsed"),
                        employee.leaveBalance.as("remainingDays")
                ))
                .from(employee)
                .leftJoin(employee.department, department)
                .orderBy(department.deptName.asc(), employee.position.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(employee.count())
                .from(employee)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    // 휴가 유형별 사용 일수 계산
    private JPQLQuery<Double> calculateVacationDays(VacationType type) {

        // 반차인 경우 휴가 개수를 카운트 한 뒤 0.5를 곱해서 사용 일수 계산
        if (type == VacationType.HALF) {
            return JPAExpressions
                    .select(Expressions.numberTemplate(Double.class,
                            "CAST({0} * 0.5 AS DOUBLE)",
                            vacation.count()))
                    .from(vacation)
                    .where(
                            vacation.employee.eq(employee),
                            vacation.type.eq(type),
                            vacation.status.eq(VacationStatus.APPROVED)
                    );
        }

        // 반차를 제외한 경우, endDate와 startDate의 차이로 휴가 일수 계산
        return JPAExpressions
                .select(Expressions.numberTemplate(
                        Double.class,
                        "SUM(DATEDIFF({0}, {1}) + 1)",
                        vacation.endDate,
                        vacation.startDate
                ))
                .from(vacation)
                .where(
                        vacation.employee.eq(employee),
                        vacation.type.eq(type),
                        vacation.status.eq(VacationStatus.APPROVED)
                )
                .groupBy(employee.id);  // 직원별 그룹화
    }

    // 총 사용 일수 계산
    private JPQLQuery<Double> calculateTotalUsedDays() {
        return JPAExpressions
                .select(
                        new CaseBuilder()
                                .when(vacation.type.eq(VacationType.HALF))
                                .then(0.5)
                                .otherwise(
                                        Expressions.numberTemplate(
                                                Double.class,
                                                "DATEDIFF({0}, {1}) + 1",
                                                vacation.endDate,
                                                vacation.startDate
                                        )
                                ).sum()
                )
                .from(vacation)
                .where(vacation.employee.eq(employee)
                        .and(vacation.status.eq(VacationStatus.APPROVED)));
    }
}
