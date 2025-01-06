package com.hr_handlers.admin.repository.salary;

import com.hr_handlers.salary.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdminSalaryRepository extends JpaRepository<Salary, Long>, AdminSalaryRepositoryCustom {
}