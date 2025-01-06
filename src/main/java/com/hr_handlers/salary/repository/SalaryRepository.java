package com.hr_handlers.salary.repository;

import com.hr_handlers.salary.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long>, SalaryRepositoryCustom {
}