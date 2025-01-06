package com.hr_handlers.chat.service;

import com.hr_handlers.chat.dto.ChatInviteResponseDto;
import com.hr_handlers.chat.dto.ChatResponseDto;
import com.hr_handlers.chat.entity.Chat;
import com.hr_handlers.chat.entity.ChatRoom;
import com.hr_handlers.chat.mapper.ChatMapper;
import com.hr_handlers.chat.repository.ChatMessageRepository;
import com.hr_handlers.chat.repository.ChatRepository;
import com.hr_handlers.chat.repository.ChatRoomRepository;
import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.employee.repository.EmployeeRepository;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final EmployeeRepository empRepository;
    private final ChatMapper chatMapper;

    // 채팅 참여 추가 -> ChatRoom에서 추가를 받을 것
    public SuccessResponse<ChatResponseDto> enterChatRoom(Long chatRoomId, String empNo) {
        Employee employee = empRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));

        return SuccessResponse.of("채팅 참여에 성공했습니다.", chatMapper.toChatResponseDto(chatRepository.insertChat(chatRoomId, employee.getId())));
    }
    
    // 참여한 채팅 목록 조회
    public SuccessResponse<Page<ChatResponseDto>> getChats(String empNo, String keyword, Pageable pageable) {
        Employee employee = empRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));
        return SuccessResponse.of(
                "참여한 채팅 목록 조회에 성공했습니다.",
                chatRepository.findByEmployeeId(employee.getId(), keyword, pageable)
        );
    }

    // 채팅방 참여인원 조회
    public SuccessResponse<List<ChatResponseDto>> getJoinedEmployees(Long chatRoomId) {
        return SuccessResponse.of(
                "채팅방에 참여한 사원 목록 조회에 성공했습니다.",
                chatRepository.findJoinedEmployees(chatRoomId)
        );
    }

    // 채팅방 초대 목록 조회
    public SuccessResponse<List<ChatInviteResponseDto>> getNotExistsChat(Long chatRoomId, String keyword) {
        return SuccessResponse.of(
                "채팅방 초대 목록 조회에 성공했습니다.",
                chatRepository.findEmployeesNotInChat(chatRoomId, keyword)
        );
    }

    // 채팅방 참여 인원 수 조회
    public SuccessResponse<Long> getJoinedEmployeesCount(Long chatRoomId) {
        return SuccessResponse.of(
                "채팅방 인원 수 조회에 성공했습니다.",
                chatRepository.countChatByChatRoomId(chatRoomId)
        );
    }

    // 채팅방 퇴장
    @Transactional
    public SuccessResponse<Long> exitChatRoom(Long chatRoomId, String empNo) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new GlobalException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        Employee employee = empRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // Chat 객체 찾기
        Chat chat = chatRepository.findByChatId(chatRoom.getId(), employee.getId());

        // Chat 객체가 없는 경우
        if (chat == null) {
            throw new GlobalException(ErrorCode.CHAT_NOT_FOUND); 
        }

        chatRepository.delete(chat);

        // 채팅방 인원이 0명이면 채팅방 삭제
        if (chatRepository.countChatByChatRoomId(chatRoomId) == 0) {
            chatMessageRepository.deleteChatMessagesByChatRoomId(chatRoomId); // 채팅방 삭제 전 메시지 모두 삭제
            chatRepository.deleteChatByChatRoomId(chatRoomId); // 채팅 참여 삭제
            chatRoomRepository.deleteById(chatRoomId); // 채팅방 삭제
        }

        return SuccessResponse.of(
                "채팅방 퇴장에 성공했습니다.",
                chatRoomId
        );
    }
}