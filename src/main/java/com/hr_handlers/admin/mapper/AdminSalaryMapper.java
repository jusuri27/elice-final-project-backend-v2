package com.hr_handlers.admin.mapper;

import com.hr_handlers.admin.dto.salary.request.AdminSalarySearchRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

@Mapper
public interface AdminSalaryMapper {

    List<T> getYearlySummaryByIndividual(AdminSalarySearchRequestDto search);
    List<T> getMonthlySummaryByIndividual(AdminSalarySearchRequestDto search);
    List<T> getYearlySummaryByDepartment(AdminSalarySearchRequestDto search);
    List<T> getMonthlySummaryByDepartment(AdminSalarySearchRequestDto search);
}
