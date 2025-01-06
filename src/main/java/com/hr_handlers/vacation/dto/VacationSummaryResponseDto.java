package com.hr_handlers.vacation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VacationSummaryResponseDto {
    private Double remaining;
    private Double used;
    private Double pending;
}
