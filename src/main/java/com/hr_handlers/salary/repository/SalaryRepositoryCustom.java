package com.hr_handlers.salary.repository;


import com.hr_handlers.salary.dto.response.SalaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SalaryRepositoryCustom {

    Page<SalaryResponse> findSalaryByEmployeeNumber(Pageable pageable, String employeeNumber);
}
