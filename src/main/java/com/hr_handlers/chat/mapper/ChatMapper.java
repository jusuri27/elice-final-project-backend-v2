package com.hr_handlers.chat.mapper;

import com.hr_handlers.chat.dto.ChatResponseDto;
import com.hr_handlers.chat.entity.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "chatRoom.id", target = "chatRoomId")
    @Mapping(source = "chatRoom.title", target = "title")
    @Mapping(source = "chatRoom.isSecret", target = "isSecret")
    @Mapping(source = "employee.empNo", target = "empNo")
    @Mapping(source = "employee.name", target = "empName")
    @Mapping(source = "employee.position", target = "empPosition")
    @Mapping(source = "employee.department.deptName", target = "deptName")
    ChatResponseDto toChatResponseDto(Chat chat);

}
