package com.hr_handlers.chat.service;

import com.hr_handlers.chat.dto.ChatRoomRequestDto;
import com.hr_handlers.chat.dto.ChatRoomResponseDto;
import com.hr_handlers.chat.entity.ChatRoom;
import com.hr_handlers.chat.mapper.ChatRoomMapper;
import com.hr_handlers.chat.repository.ChatMessageRepository;
import com.hr_handlers.chat.repository.ChatRepository;
import com.hr_handlers.chat.repository.ChatRoomRepository;
import com.hr_handlers.employee.repository.EmployeeRepository;
import com.hr_handlers.global.dto.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRepository chatRepository;
    private final EmployeeRepository empRepository;
    private final ChatRoomMapper chatRoomMapper;

    // 채팅방 생성
    public SuccessResponse<ChatRoomResponseDto> createChatRoom(ChatRoomRequestDto chatRoomRequestDto, String empNo) {
        ChatRoom chatRoom = ChatRoom.builder()
                .title(chatRoomRequestDto.getTitle())
                .isSecret(chatRoomRequestDto.getIsSecret())
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        ChatRoomResponseDto chatRoomResponseDto = chatRoomMapper.toChatRoomResponseDto(savedChatRoom);

        chatRepository.insertChat(chatRoomResponseDto.getChatRoomId(), empRepository.findByEmpNo(empNo).get().getId());

        return SuccessResponse.of("채팅방 생성 성공", chatRoomResponseDto);
    }

    // 채팅방 조회
    public SuccessResponse<Page<ChatRoomResponseDto>> getChatRooms(String keyword, Pageable pageable) {
        return SuccessResponse.of(
                "생성된 채팅방 목록 조회 성공",
                chatRoomRepository.findPublicChatRoom(keyword, pageable)
                );
    }

    // 채팅방 삭제
    @Transactional
    public SuccessResponse<Long> deleteChatRoom(Long chatRoomId) {
        chatMessageRepository.deleteChatMessagesByChatRoomId(chatRoomId); // 채팅방 삭제 전 메시지 모두 삭제
        chatRepository.deleteChatByChatRoomId(chatRoomId); // 채팅 참여 삭제
        chatRoomRepository.deleteById(chatRoomId); // 채팅방 삭제
        return SuccessResponse.of("채팅방 삭제 성공", chatRoomId);
    }
}
