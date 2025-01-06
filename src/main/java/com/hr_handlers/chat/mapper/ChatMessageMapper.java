package com.hr_handlers.chat.mapper;

import com.hr_handlers.chat.dto.ChatMessageResponseDto;
import com.hr_handlers.chat.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    // Entity -> DTO
    @Mapping(source = "id", target = "messageId")  // id -> messageId
    @Mapping(source = "chatRoom.id", target = "chatRoomId")  // chatRoom의 id -> chatRoomId
    @Mapping(source = "employee.name", target = "employeeName") // employee의 name -> employeeName
    @Mapping(source = "employee.empNo", target = "empNo")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    ChatMessageResponseDto toChatMessageResponseDto(ChatMessage chatMessage);
}
