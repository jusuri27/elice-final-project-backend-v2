package com.hr_handlers.chat.repository;

import com.hr_handlers.chat.dto.ChatRoomResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hr_handlers.chat.entity.QChatRoom.chatRoom;

@Repository
@AllArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ChatRoomResponseDto> findPublicChatRoom(String keyword, Pageable pageable) {

        BooleanExpression condition = (keyword != null && !keyword.trim().isEmpty())
                ? chatRoom.title.containsIgnoreCase(keyword)
                : null; // 전체 조회


        List<ChatRoomResponseDto> chatRoomResponseDtos = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ChatRoomResponseDto.class,
                                chatRoom.id,
                                chatRoom.title,
                                chatRoom.isSecret
                                )
                )
                .from(chatRoom)
                .where(
                        chatRoom.isSecret.eq("N")
                                .and(condition)
                )
                .orderBy(chatRoom.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(chatRoom.count())
                .from(chatRoom)
                .where(
                        chatRoom.isSecret.eq("N")
                                .and(condition)
                )
                .fetchOne();

        return new PageImpl<>(chatRoomResponseDtos, pageable, total);
    }
}
