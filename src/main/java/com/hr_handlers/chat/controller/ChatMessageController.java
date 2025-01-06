package com.hr_handlers.chat.controller;

import com.hr_handlers.chat.dto.ChatMessageRequestDto;
import com.hr_handlers.chat.dto.ChatMessageResponseDto;
import com.hr_handlers.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    
    // 메시지 전송
    @MessageMapping("/message/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") Long chatRoomId,
                            @Payload ChatMessageRequestDto chatMessageRequestDto,
                            @Header("simpUser") Principal principal) {
        ChatMessageResponseDto chatMessageResponseDto = chatMessageService.sendMessage(chatRoomId, chatMessageRequestDto, principal.getName());
        messagingTemplate.convertAndSend("/topic/message/%s".formatted(chatRoomId), chatMessageResponseDto);
    }
    
    // 메시지 수정
    @MessageMapping("/message/update/{chatRoomId}")
    public void updateMessage(@DestinationVariable("chatRoomId") Long chatRoomId,
                              @Payload ChatMessageRequestDto chatMessageRequestDto,
                              @Header("simpUser") Principal principal) {
        ChatMessageResponseDto chatMessageResponseDto = chatMessageService.updateMessage(chatMessageRequestDto, principal.getName());
        messagingTemplate.convertAndSend("/topic/message/update/%s".formatted(chatRoomId), chatMessageResponseDto);
    }

    // 메시지 삭제
    @MessageMapping("/message/delete/{chatRoomId}")
    public void deleteMessage(@DestinationVariable("chatRoomId") Long chatRoomId,
                              @Payload ChatMessageRequestDto chatMessageRequestDto,
                              @Header("simpUser") Principal principal) {
        ChatMessageResponseDto chatMessageResponseDto = chatMessageService.deleteMessage(chatMessageRequestDto, principal.getName());
        messagingTemplate.convertAndSend("/topic/message/delete/%s".formatted(chatRoomId), chatMessageResponseDto.getMessageId());
    }
}
