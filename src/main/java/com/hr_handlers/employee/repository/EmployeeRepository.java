package com.hr_handlers.employee.repository;

import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.employee.repository.custom.EmployeeCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeCustomRepository {
    Optional<Employee> findByEmpNo(String empNo);

    boolean existsByEmpNoAndEmail(String empNo, String email);

    Optional<Employee> findByEmpNoAndEmail(String empNo, String email);

    List<Employee> findAllByEmpNoIn(List<String> empNos);
}