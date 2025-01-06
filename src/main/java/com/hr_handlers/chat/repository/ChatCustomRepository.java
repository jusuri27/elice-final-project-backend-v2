package com.hr_handlers.chat.repository;

import com.hr_handlers.chat.dto.ChatInviteResponseDto;
import com.hr_handlers.chat.dto.ChatResponseDto;
import com.hr_handlers.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatCustomRepository {

    // 참여한 채팅 목록 조회
    Page<ChatResponseDto> findByEmployeeId(Long employeeId, String keyword, Pageable pageable);

    // Chat 객체 찾기
    Chat findByChatId(Long chatRoomId, Long employeeId);

    // 채팅 참여
    Chat insertChat(Long chatRoomId, Long employeeId);

    // 채팅방 참여 사원 목록 조회
    List<ChatResponseDto> findJoinedEmployees(Long chatRoomId);

    // 채팅방에 참여하고 있지 않은 사원 조회
    List<ChatInviteResponseDto> findEmployeesNotInChat(Long chatRoomId, String keyword);

    // 채팅 참여 삭제
    void deleteChatByChatRoomId(Long chatRoomId);

    // 채팅 참여 인원 조회
    Long countChatByChatRoomId(Long chatRoomId);
}
