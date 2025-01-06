package com.hr_handlers.chat.repository;

import com.hr_handlers.chat.entity.ChatMessage;

import java.util.List;

public interface ChatMessageCustomRepository {

    // 채팅방 메시지 내역 조회
    List<ChatMessage> findChatMessagesByChatRoomId(Long chatRoomId);

    // 채팅 메시지 삭제
    void deleteChatMessagesByChatRoomId(Long chatRoomId);
}
