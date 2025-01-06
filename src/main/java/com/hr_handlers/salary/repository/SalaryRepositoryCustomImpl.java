package com.hr_handlers.salary.repository;

import com.hr_handlers.employee.entity.QEmployee;
import com.hr_handlers.salary.dto.response.SalaryResponse;
import com.hr_handlers.salary.entity.QSalary;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class SalaryRepositoryCustomImpl implements SalaryRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QSalary salary = QSalary.salary;
    private final QEmployee employee = QEmployee.employee;

    public Page<SalaryResponse> findSalaryByEmployeeNumber(Pageable pageable, String employeeNumber) {
        List<SalaryResponse> results = queryFactory
                .select(Projections.constructor(
                        SalaryResponse.class,
                        salary.id,
                        employee.id,
                        employee.department.deptName,
                        employee.position,
                        employee.name,
                        salary.basicSalary,
                        salary.deduction,
                        salary.netSalary,
                        salary.payDate,
                        salary.createdAt,
                        salary.updatedAt
                ))
                .from(salary)
                .leftJoin(employee).on(salary.employee.empNo.eq(employee.empNo)).fetchJoin()
                .where(
                        salary.employee.empNo.eq(employeeNumber)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(salary.payDate.asc())
                .fetch();

        // 개수 조회
        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(salary)
                .leftJoin(employee).on(salary.employee.empNo.eq(employee.empNo)).fetchJoin()
                .where(
                        salary.employee.empNo.eq(employeeNumber)
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);
    }
}
