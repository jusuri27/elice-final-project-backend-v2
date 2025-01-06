package com.hr_handlers.admin.repository.salary;

import com.hr_handlers.admin.dto.salary.request.AdminSalaryExcelDownloadRequestDto;
import com.hr_handlers.admin.dto.salary.request.AdminSalarySearchRequestDto;
import com.hr_handlers.admin.dto.salary.response.AdminSalaryResponseDto;
import com.hr_handlers.employee.entity.QEmployee;
import com.hr_handlers.salary.entity.QSalary;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AdminSalaryRepositoryCustomImpl implements AdminSalaryRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QSalary salary = QSalary.salary;
    private final QEmployee employee = QEmployee.employee;


    public List<AdminSalaryResponseDto> findAllSalary() {
        return queryFactory
                .select(Projections.constructor(
                        AdminSalaryResponseDto.class,
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
                .leftJoin(employee).on(salary.employee.id.eq(employee.id)).fetchJoin()
                .orderBy(salary.payDate.asc())
                .fetch();
    }

    public Page<AdminSalaryResponseDto> searchSalaryByFilter(Pageable pageable, AdminSalarySearchRequestDto search) {
        List<AdminSalaryResponseDto> results = queryFactory
                .select(Projections.constructor(
                        AdminSalaryResponseDto.class,
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
                .leftJoin(employee).on(salary.employee.id.eq(employee.id)).fetchJoin()
                .where(
                        eqPosition(search.getPosition()),
                        likeName(search.getName()),
                        eqDeptName(search.getDeptName()),
                        betweenPayDate(search.getStartDate(), search.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getAllOrderSpecifiers(pageable).stream().toArray(OrderSpecifier[]::new))
                .fetch();

        // 개수 조회
        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(salary)
                .leftJoin(employee).on(salary.employee.id.eq(employee.id))
                .where(
                        eqPosition(search.getPosition()),
                        likeName(search.getName()),
                        eqDeptName(search.getDeptName()),
                        betweenPayDate(search.getStartDate(), search.getEndDate())
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public List<AdminSalaryExcelDownloadRequestDto> searchSalaryForExcel(AdminSalarySearchRequestDto search) {
        return queryFactory
                .select(Projections.constructor(
                        AdminSalaryExcelDownloadRequestDto.class,
                        employee.department.deptName,
                        employee.position,
                        employee.name,
                        salary.basicSalary,
                        salary.deduction,
                        salary.netSalary,
                        salary.payDate
                ))
                .from(salary)
                .leftJoin(employee).on(salary.employee.id.eq(employee.id)).fetchJoin()
                .where(
                        eqPosition(search.getPosition()),
                        likeName(search.getName()),
                        eqDeptName(search.getDeptName()),
                        betweenPayDate(search.getStartDate(), search.getEndDate())
                )
                .orderBy(employee.name.asc(), salary.payDate.asc())
                .fetch();
    }

    private BooleanExpression likeName(String name) {
        return StringUtils.hasText(name) ? employee.name.like("%%%s%%".formatted(name)) : null;
    }

    private BooleanExpression eqPosition(String position) {
        return StringUtils.hasText(position) ? employee.position.eq(position) : null;
    }

    private BooleanExpression eqDeptName(String deptName) {
        return StringUtils.hasText(deptName) ? employee.department.deptName.eq(deptName) : null;
    }

    private BooleanExpression betweenPayDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return salary.payDate.between(startDate, endDate);
    }

    // 동적 정렬을 위한 다중 OrderSpecifier 추출 method
    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable){
        List<OrderSpecifier> orders = new ArrayList<>();

        if(!pageable.getSort().isEmpty()) {
            for(Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                String prop = order.getProperty();
                PathBuilder orderPath;
                // employee, salary 두개 테이블에서 정렬가능한지 확인용
                if (prop.startsWith("employee.")) {
                    orderPath = new PathBuilder<>(employee.getType(), "employee");
                    prop = prop.substring("employee.".length());
                } else {
                    orderPath = new PathBuilder<>(salary.getType(), "salary");
                    prop = prop.substring("salary.".length());
                }
                orders.add(new OrderSpecifier(direction,orderPath.get(prop)));
            }
        }

        return orders;
    }
}
