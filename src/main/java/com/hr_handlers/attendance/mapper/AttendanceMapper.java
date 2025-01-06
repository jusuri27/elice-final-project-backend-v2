package com.hr_handlers.attendance.mapper;

import com.hr_handlers.attendance.dto.CheckInResponseDto;
import com.hr_handlers.attendance.dto.CheckOutResponseDto;
import com.hr_handlers.attendance.entity.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    CheckInResponseDto toCheckInResponse(Attendance attendance);

    @Mapping(source = "employee.id", target = "employeeId")
    CheckOutResponseDto toCheckOutResponse(Attendance attendance);
}
