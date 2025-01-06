package com.hr_handlers.admin.repository.employee;

import com.hr_handlers.employee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminDepartmentRepository extends JpaRepository<Department, Long>, AdminDepartmentCustomRepository {
    Optional<Department> findByDeptName(String deptName);

    boolean existsByDeptName(String deptName);
}