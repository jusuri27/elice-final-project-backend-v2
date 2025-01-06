package com.hr_handlers.admin.repository.employee;

import com.hr_handlers.admin.dto.employee.request.AdminEmployeeUpdateRequestDto;
import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.global.dto.SearchRequestDto;
import org.springframework.data.domain.Page;

public interface AdminEmployeeCustomRepository {
    Page<Employee> findEmpByName(SearchRequestDto requestDto);

    void updateEmp(String empNo, AdminEmployeeUpdateRequestDto updateRequest);

    void deleteEmp(String empNo);
}