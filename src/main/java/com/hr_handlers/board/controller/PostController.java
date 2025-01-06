package com.hr_handlers.board.controller;

import com.hr_handlers.board.dto.*;
import com.hr_handlers.board.service.PostService;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Slf4j
@Tag(name = "게시글", description = "게시글 관련 API")
public class PostController {

    private final PostService postService;

    @Operation(summary = "전체 게시글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public SuccessResponse<PostListResponseDto> getAllPosts(
            @PageableDefault(size = 10) Pageable pageable) {
        return postService.getAllPosts(pageable);
    }

    // 특정 게시글 상세 조회
    @Operation(summary = "게시글 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public SuccessResponse<PostDetailResponseDto> getPostById(@PathVariable("id") Long id) {
        return postService.getPostById(id);
    }

    // 게시글 생성
    @Operation(summary = "게시글 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작성 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 생성 실패 또는 사원을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public SuccessResponse<PostActionResponseDto> createPost(@RequestBody PostRequestDto request, Authentication authentication) {
        return postService.createPost(request, authentication.getName());
    }


    // 특정 게시글 수정
    @Operation(summary = "게시글 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없거나 수정 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public SuccessResponse<PostActionResponseDto> updatePost(@PathVariable("id") Long id, @RequestBody PostRequestDto request) {
        return postService.updatePost(id, request);
    }

    // 특정 게시글 삭제
    @Operation(summary = "게시글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없거나 삭제 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public SuccessResponse<String> deletePost(@PathVariable("id") Long id) {
        return postService.deletePost(id);
    }

    // 공지사항 조회
    @Operation(summary = "공지사항 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "공지사항 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/notices")
    public SuccessResponse<PostListResponseDto> getAllNotices(
            @PageableDefault(size = 5) Pageable pageable) {
        return postService.getAllNotices(pageable);
    }
}
