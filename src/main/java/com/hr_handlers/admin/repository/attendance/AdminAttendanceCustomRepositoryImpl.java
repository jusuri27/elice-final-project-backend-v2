package com.hr_handlers.admin.repository.attendance;

import com.hr_handlers.admin.dto.attendance.AdminAttendanceResponseDto;
import com.hr_handlers.admin.dto.attendance.AdminAttendanceSearchDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static com.hr_handlers.attendance.entity.QAttendance.attendance;
import static com.hr_handlers.employee.entity.QDepartment.department;
import static com.hr_handlers.employee.entity.QEmployee.employee;

@Repository
public class AdminAttendanceCustomRepositoryImpl implements AdminAttendanceCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public AdminAttendanceCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory){
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<AdminAttendanceResponseDto> findAllAttendance(
            AdminAttendanceSearchDto searchDto,
            Pageable pageable
    ) {

        NumberExpression<Integer> totalHours = attendance.checkOutTime.hour().subtract(attendance.checkInTime.hour());
        NumberExpression<Integer> totalMinutes = attendance.checkOutTime.minute().subtract(attendance.checkInTime.minute());
        BooleanBuilder whereCondition = new BooleanBuilder();

        attendanceValid(searchDto, whereCondition);

        List<AdminAttendanceResponseDto> content = jpaQueryFactory
                .select(Projections.constructor(AdminAttendanceResponseDto.class,
                        attendance.id,
                        employee.position,
                        department.deptName,
                        employee.name,
                        attendance.status,
                        attendance.checkInTime.stringValue(),
                        attendance.checkOutTime.stringValue(),
                        ExpressionUtils.as(
                                totalHours.multiply(60).add(totalMinutes),
                                "workingTime"
                        )
                ))
                .from(attendance)
                .join(attendance.employee, employee)
                .join(employee.department, department)
                .where(whereCondition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(attendance.count())
                .from(attendance)
                .join(attendance.employee, employee)
                .join(employee.department, department)
                .where(whereCondition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);

        }


    private static void attendanceValid(AdminAttendanceSearchDto searchDto, BooleanBuilder whereCondition) {
        // 날짜 조건
        if (searchDto.getCheckInTime() != null && searchDto.getCheckOutTime() != null) {
            whereCondition.and(attendance.checkInTime.between(
                    searchDto.getCheckInTime(),
                    searchDto.getCheckOutTime()
            ));
        }

        // 부서 조건
        if (StringUtils.hasText(searchDto.getDeptName())) {
            whereCondition.and(department.deptName.contains(searchDto.getDeptName()));
        }

        // 직위 조건
        if (StringUtils.hasText(searchDto.getPosition())) {
            whereCondition.and(employee.position.contains(searchDto.getPosition()));
        }

        // 이름 조건
        if (StringUtils.hasText(searchDto.getName())) {
            whereCondition.and(employee.name.contains(searchDto.getName()));
        }
    }
}
