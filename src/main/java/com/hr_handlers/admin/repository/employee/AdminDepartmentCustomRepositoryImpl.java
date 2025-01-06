package com.hr_handlers.admin.repository.employee;

import com.hr_handlers.employee.entity.Department;
import com.hr_handlers.employee.entity.QDepartment;
import com.hr_handlers.employee.entity.QEmployee;
import com.hr_handlers.global.dto.SearchRequestDto;
import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminDepartmentCustomRepositoryImpl implements AdminDepartmentCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public Page<Department> findDeptByName(SearchRequestDto requestDto) {
       QDepartment department = QDepartment.department;

        BooleanExpression condition = (requestDto.getKeyword() != null && !requestDto.getKeyword().trim().isEmpty())
                ? department.deptName.containsIgnoreCase(requestDto.getKeyword())
                : null; // 전체 조회

        List<Department> results = queryFactory
                .selectFrom(department)
                .where(condition) // 검색 없으면 전체 조회, 있으면 필터링
                .offset(requestDto.getPage() * requestDto.getSize())
                .limit(requestDto.getSize())
                .orderBy(department.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(department.count())
                .from(department)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(results, PageRequest.of(requestDto.getPage(), requestDto.getSize()), total);
    }

    @Override
    @Transactional
    public void updateDept(Long id, String deptName) {
        long updatedCount = queryFactory
                .update(QDepartment.department)
                .where(QDepartment.department.id.eq(id))
                .set(QDepartment.department.deptName, deptName)
                .execute();

        if (updatedCount == 0) {
            throw new GlobalException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Override
    @Transactional
    public void deleteDept(Long id) {
        queryFactory
                .update(QEmployee.employee)
                .set(QEmployee.employee.department, (Department) null)
                .where(QEmployee.employee.department.id.eq(id))
                .execute();

        long deletedCount = queryFactory
                .delete(QDepartment.department)
                .where(QDepartment.department.id.eq(id))
                .execute();

        if (deletedCount == 0) {
            throw new GlobalException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }

        entityManager.flush();
        entityManager.clear();
    }
}