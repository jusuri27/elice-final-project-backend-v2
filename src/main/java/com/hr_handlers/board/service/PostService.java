package com.hr_handlers.board.service;


import com.hr_handlers.board.dto.*;
import com.hr_handlers.board.entity.HashTag;
import com.hr_handlers.board.enums.PostType;
import com.hr_handlers.board.repository.PostRepository;
import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.employee.repository.EmployeeRepository;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import com.hr_handlers.global.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.hr_handlers.board.mapper.PostMapper;
import com.hr_handlers.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final EmployeeRepository empRepository;

    private final S3Service s3Service;



    // 게시글 목록 조회
    public SuccessResponse<PostListResponseDto> getAllPosts(Pageable pageable) {
        // 활성화된 게시글만 조회
        Page<Post> postsPage = postRepository.findActivePostsByType(PostType.POST, pageable);

        List<PostResponseDto> response = postsPage.isEmpty()
                ? Collections.emptyList() // 빈 리스트
                : postsPage.getContent().stream()
                .map(postMapper::toPostResponseDto)
                .toList();

        return SuccessResponse.of("게시글 목록 조회 성공",
                new PostListResponseDto(response, postsPage.getTotalElements()));
    }

    // 게시글 상세 조회
    public SuccessResponse<PostDetailResponseDto> getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_NOT_FOUND));

        return SuccessResponse.of("게시글 상세 조회 성공", postMapper.toPostDetailResponseDto(post));
    }


    // 게시글 생성
    @Transactional
    public SuccessResponse<PostActionResponseDto> createPost(PostRequestDto request, String empNo) {
        Employee employee = empRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));

        log.info("Received Post Request: {}", request); // 요청 값 출력

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .employee(employee)
                .isDelete("N")
                .postType(request.getPostType()) // POST 또는 NOTICE 설정
                .build();

        if (request.getHashtagContent() != null) {
            post.setHashtagContent(request.getHashtagContent().stream()
                    .map(tag -> HashTag.builder()
                            .hashtagContent(tag)
                            .post(post)
                            .build())
                    .toList());
        }

        postRepository.save(post);

        return SuccessResponse.of("게시글 생성 성공",
                new PostActionResponseDto(post.getId(), post.getCreatedAt().toString()));
    }

    @Transactional
    public SuccessResponse<PostActionResponseDto> updatePost(Long postId, PostRequestDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_NOT_FOUND));

        // 기존 해시태그 컬렉션을 비우고 새 값을 추가
        List<HashTag> existingHashTags = post.getHashtagContent();
        existingHashTags.clear();

        if (dto.getHashtagContent() != null) {
            List<HashTag> newHashTags = dto.getHashtagContent().stream()
                    .map(tag -> HashTag.builder()
                            .hashtagContent(tag)
                            .post(post) // 부모 엔티티 지정
                            .build())
                    .toList();
            existingHashTags.addAll(newHashTags);
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setImageUrl(dto.getImageUrl());

        // 컬렉션 수정 후 저장 없이 변경 사항 반영
        return SuccessResponse.of("게시글 수정 성공",
                new PostActionResponseDto(post.getId(), post.getUpdatedAt().toString()));
    }



    // 게시글 삭제
    @Transactional
    public SuccessResponse<String> deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_NOT_FOUND));
        try {
            // content에서 이미지 URL 추출
            if (post.getContent() != null && !post.getContent().isEmpty()) {
                extractImageUrlsFromContent(post.getContent()).forEach(s3Service::deleteFile);
            }

            // 게시글 논리 삭제
            post.setIsDelete("Y");
            postRepository.save(post);

            return SuccessResponse.of("게시글 삭제 성공", "게시글이 삭제되었습니다.");
        } catch (Exception e) {
            log.error("게시글 삭제 실패: ", e);
            throw new GlobalException(ErrorCode.POST_DELETE_FAILED);
        }
    }

    public SuccessResponse<PostListResponseDto> getAllNotices(Pageable pageable) {
        Page<Post> noticesPage = postRepository.findActivePostsByType(PostType.NOTICE, pageable);

        List<PostResponseDto> response = noticesPage.isEmpty()
                ? Collections.emptyList()
                : noticesPage.getContent().stream()
                .map(postMapper::toPostResponseDto)
                .toList();

        return SuccessResponse.of("공지사항 조회 성공",
                new PostListResponseDto(response, noticesPage.getTotalElements()));
    }


    // content에서 이미지 URL 추출 메서드
    private List<String> extractImageUrlsFromContent(String content) {
        List<String> imageUrls = new ArrayList<>();
        Document document = Jsoup.parse(content); // HTML 파싱
        Elements imgElements = document.select("figure img"); // 모든 <figure> 내부의 <img> 태그 선택

        for (Element img : imgElements) {
            String src = img.attr("src"); // src 속성 값 추출
            if (src != null && !src.isEmpty()) {
                imageUrls.add(src);
            }
        }
        return imageUrls;
    }

}
