package com.hr_handlers.chat.repository;

import com.hr_handlers.chat.dto.ChatRoomResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomCustomRepository {

    // 채팅방 전체 목록 조회
    Page<ChatRoomResponseDto> findPublicChatRoom(String keyword, Pageable pageable);
}
