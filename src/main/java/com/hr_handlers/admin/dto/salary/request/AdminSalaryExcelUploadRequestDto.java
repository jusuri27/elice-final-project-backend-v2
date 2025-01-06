package com.hr_handlers.admin.dto.salary.request;

import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.global.utils.ExcelColumn;
import com.hr_handlers.salary.entity.Salary;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;

@Getter
public class AdminSalaryExcelUploadRequestDto {
    @ExcelColumn(headerName = "사원Id", sort=1)
    private String employeeId;
    @ExcelColumn(headerName = "이름", sort=2) // 실제로 db insert시 사용안하지만 확인용도
    private String name;
    @ExcelColumn(headerName = "지급총액", sort=3)
    private int basicSalary;
    @ExcelColumn(headerName = "공제총액", sort=4)
    private int deduction;
    @ExcelColumn(headerName = "실지급액", sort=5)
    private int netSalary;
    @ExcelColumn(headerName = "급여 지급일", sort=6)
    private LocalDate payDate;

    public void fillUpFromRow(Row row) {
        this.employeeId = String.valueOf((long) row.getCell(0).getNumericCellValue());
        this.name = row.getCell(1).getStringCellValue();
        this.basicSalary = (int) row.getCell(2).getNumericCellValue();
        this.deduction = (int) row.getCell(3).getNumericCellValue();
        this.netSalary = (int) row.getCell(4).getNumericCellValue();
        this.payDate = row.getCell(5).getLocalDateTimeCellValue().toLocalDate();
    }

    public Salary toCreateEntity(Employee employee) {
        return Salary.builder()
                .employee(employee)
                .basicSalary(this.basicSalary)
                .deduction(this.deduction)
                .netSalary(this.netSalary)
                .payDate(this.payDate)
                .year(String.valueOf(this.payDate.getYear()))
                .build();
    }

}
