package com.hr_handlers.admin.repository.employee;

import com.hr_handlers.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminEmployeeRepository extends JpaRepository<Employee, Long>, AdminEmployeeCustomRepository {
    List<Employee> findByPositionAndDepartmentDeptName(String position, String deptName);
}