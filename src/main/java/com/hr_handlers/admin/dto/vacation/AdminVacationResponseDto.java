package com.hr_handlers.admin.dto.vacation;

import com.hr_handlers.vacation.entity.VacationStatus;
import com.hr_handlers.vacation.entity.VacationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminVacationResponseDto {
    private Long id;
    private String position;
    private String deptName;
    private String name;
    private String period;
    private VacationType type;
    private Double use;
    private VacationStatus status;
}
