package com.hr_handlers.board.repository;

import com.hr_handlers.board.entity.Post;
import com.hr_handlers.board.entity.QPost;
import com.hr_handlers.board.enums.PostType;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory queryFactory;


    /**
     * 공통 메서드: 특정 PostType의 게시글 조회
     */
    @Override
    public Page<Post> findActivePostsByType(PostType postType, Pageable pageable) {
        QPost post = QPost.post;

        JPAQuery<Post> query = queryFactory.selectFrom(post)
                .where(
                        post.isDelete.eq("N"),       // 삭제되지 않은 게시글만
                        post.postType.eq(postType)   // PostType 조건 추가
                );

        // 페이지네이션 적용
        List<Post> posts = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 계산
        long total = query.fetchCount();

        return new PageImpl<>(posts, pageable, total);
    }

}
