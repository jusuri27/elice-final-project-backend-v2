package com.hr_handlers.vacation.mapper;

import com.hr_handlers.vacation.dto.ApprovedVacationResponseDto;
import com.hr_handlers.vacation.dto.PendingVacationResponseDto;
import com.hr_handlers.vacation.dto.VacationResponseDto;
import com.hr_handlers.vacation.entity.Vacation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacationMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    PendingVacationResponseDto toPendingVacationResponse(Vacation vacation);

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "approvedAt", target = "approvedAt")
    @Mapping(source = "approver", target = "approver")
    ApprovedVacationResponseDto toApprovedVacationResponse(Vacation vacation);

    @Mapping(source = "employee.id", target = "employeeId")
    VacationResponseDto toVacationResponse(Vacation vacation);
}
