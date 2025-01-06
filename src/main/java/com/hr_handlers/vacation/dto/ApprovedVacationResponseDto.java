package com.hr_handlers.vacation.dto;

import com.hr_handlers.vacation.entity.VacationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedVacationResponseDto {
    private String docNum;
    private String title;
    private Timestamp updatedAt;
    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp approvedAt;
    private VacationStatus status;
    private String approver;
    private Long employeeId;
}
