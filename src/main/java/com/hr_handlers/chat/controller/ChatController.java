package com.hr_handlers.chat.controller;

import com.hr_handlers.chat.dto.ChatInviteResponseDto;
import com.hr_handlers.chat.dto.ChatRequestDto;
import com.hr_handlers.chat.dto.ChatResponseDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "채팅 참여", description = "채팅 참여 관련 API")
public class ChatController {

    private final ChatService chatService;

    // 참여한 채팅 목록 조회
    @GetMapping
    @Operation(summary = "채팅 참여 목록", description = "참여한 채팅방 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "참여한 채팅방 목록 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<Page<ChatResponseDto>> getChats(
            Authentication authentication,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return chatService.getChats(authentication.getName(), keyword, pageable);
    }

    // 채팅방 참여인원 조회
    @GetMapping("/{chatRoomId}")
    @Operation(summary = "채팅 참가자 목록", description = "채팅에 참여한 참가자 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "참가자 목록 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<List<ChatResponseDto>> getJoinedEmployees(@PathVariable("chatRoomId") Long chatRoomId) {
        return chatService.getJoinedEmployees(chatRoomId);
    }

    // 채팅방 초대 목록 조회
    @GetMapping("/{chatRoomId}/invite")
    @Operation(summary = "채팅 초대 목록", description = "채팅에 참여하지 않은 초대 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "초대 목록 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<List<ChatInviteResponseDto>> getNotExistsChat(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword
    ) {
        return chatService.getNotExistsChat(chatRoomId, keyword);
    }

    // 채팅방 인원 수 조회
    @GetMapping("/{chatRoomId}/count")
    @Operation(summary = "채팅 참여 인원 수", description = "참여한 채팅방의 참가자 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "참가자 수 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<Long> getJoinedEmployeesCount(@PathVariable("chatRoomId") Long chatRoomId) {
        return chatService.getJoinedEmployeesCount(chatRoomId);
    }

    // 채팅방 초대
    @PostMapping("/{chatRoomId}")
    @Operation(summary = "채팅방 초대", description = "참여 중인 채팅방에 다른 사람을 초대합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초대 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방 초대 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<ChatResponseDto> inviteSecretChat(@PathVariable("chatRoomId") Long chatRoomId,
                                                             @RequestBody ChatRequestDto chatRequestDto) {
        return chatService.enterChatRoom(chatRoomId, chatRequestDto.getEmpNo());
    }

    // 채팅방 퇴장
    @DeleteMapping("/{chatRoomId}")
    @Operation(summary = "채팅방 퇴장", description = "참여 중인 채팅방에서 퇴장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "퇴장 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방 퇴장 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<Long> deleteChat(@PathVariable("chatRoomId") Long chatRoomId,
                                            Authentication authentication) {
        return chatService.exitChatRoom(chatRoomId, authentication.getName());
    }

}
