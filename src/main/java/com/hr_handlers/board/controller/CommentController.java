package com.hr_handlers.board.controller;

import com.hr_handlers.board.dto.CommentActionResponseDto;
import com.hr_handlers.board.dto.CommentRequestDto;
import com.hr_handlers.board.dto.CommentResponseDto;
import com.hr_handlers.board.service.CommentService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "댓글", description = "댓글 관련 API")
public class CommentController {
    private final CommentService commentService;

    // 특정 게시글의 댓글 조회
    @Operation(summary = "게시글의 댓글 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/post/{postId}/comment")
    public SuccessResponse<Page<CommentResponseDto>> getCommentsByPost(
            @PathVariable("postId") Long postId,
            @RequestParam(defaultValue = "0", value = "page") int page,
            @RequestParam(defaultValue = "10",value = "size") int size
    ) {
        return commentService.getCommentsByPost(postId, page, size);
    }


    // 댓글/대댓글 작성
    @Operation(summary = "댓글 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작성 성공"),
            @ApiResponse(responseCode = "404", description = "게시글/댓글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/post/{postId}/comment")
    public SuccessResponse<CommentActionResponseDto> createComment(
            @PathVariable("postId") Long postId,
            @RequestBody CommentRequestDto request,
            Authentication authentication
    ) {
        return commentService.createComment(postId, request, authentication.getName());
    }


    // 댓글/대댓글 수정
    @Operation(summary = "댓글 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/comment/{id}")
    public SuccessResponse<CommentActionResponseDto> updateComment(
            @PathVariable("id") Long id,
            @RequestBody CommentRequestDto request,
            Authentication authentication
    ) {
        return commentService.updateComment(id, request, authentication.getName());
    }


    // 댓글/대댓글 삭제
    @DeleteMapping("/comment/{id}")
    @Operation(summary = "댓글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse<String> deleteComment(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        commentService.deleteComment(id, authentication.getName());
        return SuccessResponse.of("댓글 삭제 성공", "댓글이 삭제되었습니다.");
    }
}
