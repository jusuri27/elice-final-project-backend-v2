package com.hr_handlers.chat.controller;

import com.hr_handlers.chat.dto.*;
import com.hr_handlers.chat.service.ChatMessageService;
import com.hr_handlers.chat.service.ChatRoomService;
import com.hr_handlers.chat.service.ChatService;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
@Tag(name = "채팅방", description = "채팅방 관련 API")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final ChatService chatService;

    // 채팅방 생성
    @PostMapping
    @Operation(summary = "채팅방 생성", description = "채팅방을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방 생성 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<ChatRoomResponseDto> createChatRoom(@RequestBody ChatRoomRequestDto chatRoomRequestDto,
                                                               Authentication authentication) {
        return chatRoomService.createChatRoom(chatRoomRequestDto, authentication.getName());
    }

    // 채팅방 목록 조회
    @GetMapping
    @Operation(summary = "채팅방 목록", description = "전체 공개인 채팅방 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방 목록 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<Page<ChatRoomResponseDto>> getChatRooms(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {

        return chatRoomService.getChatRooms(keyword, pageable);
    }
    
    // 채팅방 메시지 내역 조회
    @GetMapping("/{chatRoomId}")
    @Operation(summary = "채팅방 메시지 내역", description = "참여한 채팅방의 메시지 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "채팅 메시지 내역 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<List<ChatMessageResponseDto>> getChatMessages(@PathVariable("chatRoomId") Long chatRoomId) {
        return chatMessageService.getChatMessages(chatRoomId);
    }

    // 채팅방 참여
    @PostMapping("/{chatRoomId}")
    @Operation(summary = "채팅방 참여", description = "채팅방에 참여합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "참여 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방 참여 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<ChatResponseDto> enterChat(
            @PathVariable("chatRoomId") Long chatRoomId,
            Authentication authentication
    ) {
        return chatService.enterChatRoom(chatRoomId, authentication.getName());
    }

    // 채팅방 삭제
    @DeleteMapping("/{chatRoomId}")
    @Operation(summary = "채팅방 삭제", description = "채팅방을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방 삭제 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<Long> deleteChatRoom(@PathVariable("chatRoomId") Long chatRoomId) {
        return chatRoomService.deleteChatRoom(chatRoomId);
    }
}
