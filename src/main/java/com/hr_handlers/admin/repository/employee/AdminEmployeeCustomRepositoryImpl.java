package com.hr_handlers.admin.repository.employee;

import com.hr_handlers.admin.dto.employee.request.AdminEmployeeUpdateRequestDto;
import com.hr_handlers.employee.entity.Department;
import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.employee.entity.QEmployee;
import com.hr_handlers.employee.enums.ContractType;
import com.hr_handlers.global.dto.SearchRequestDto;
import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hr_handlers.employee.entity.QEmployee.employee;

@Repository
@RequiredArgsConstructor
public class AdminEmployeeCustomRepositoryImpl implements AdminEmployeeCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private final AdminDepartmentRepository deptRepository;

    @Override
    public Page<Employee> findEmpByName(SearchRequestDto requestDto) {
        QEmployee employee = QEmployee.employee;

        BooleanExpression condition = (requestDto.getKeyword() != null && !requestDto.getKeyword().trim().isEmpty())
                ? employee.name.containsIgnoreCase(requestDto.getKeyword())
                : null; // 전체 조회

        List<Employee> results = queryFactory
                .selectFrom(employee)
                .where(condition) // 검색 없으면 전체 조회, 있으면 필터링
                .offset(requestDto.getPage() * requestDto.getSize())
                .limit(requestDto.getSize())
                .orderBy(employee.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(employee.count())
                .from(employee)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(results, PageRequest.of(requestDto.getPage(), requestDto.getSize()), total);
    }

    @Override
    @Transactional
    public void updateEmp(String empNo, AdminEmployeeUpdateRequestDto updateRequest) {
        Department department = deptRepository.findByDeptName(updateRequest.getDeptName())
                    .orElseThrow(() -> new GlobalException(ErrorCode.DEPARTMENT_NOT_FOUND));

        JPAUpdateClause updateClause = queryFactory.update(employee)
                .where(employee.empNo.eq(empNo));

        // 조건에 따라 set 호출
        if (updateRequest.getPosition() != null) {
            updateClause.set(employee.position, updateRequest.getPosition());
        }

        if (updateRequest.getContractType() != null) {
            // 한글을 Enum으로 변환
            ContractType contractTypeEnum = ContractType.fromDescription(updateRequest.getContractType());
            updateClause.set(employee.contractType, contractTypeEnum);
        }
        if (updateRequest.getLeaveBalance() != null) {
            updateClause.set(employee.leaveBalance, updateRequest.getLeaveBalance());
        }
        if (updateRequest.getDeptName() != null) {
            updateClause.set(employee.department, department);
        }

        long updatedCount = updateClause.execute();

        if (updatedCount == 0) {
            throw new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Override
    @Transactional
    public void deleteEmp(String empNo) {
        long deletedCount = queryFactory
                .delete(employee)
                .where(employee.empNo.eq(empNo))
                .execute();

        if(deletedCount == 0){
            throw new GlobalException(ErrorCode.TODO_NOT_FOUND);
        }

        entityManager.flush();
        entityManager.clear();
    }
}