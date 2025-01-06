package com.hr_handlers.chat.repository;

import com.hr_handlers.chat.dto.ChatInviteResponseDto;
import com.hr_handlers.chat.dto.ChatResponseDto;
import com.hr_handlers.chat.entity.Chat;
import com.hr_handlers.chat.entity.QChat;
import com.hr_handlers.employee.entity.QDepartment;
import com.hr_handlers.employee.entity.QEmployee;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hr_handlers.chat.entity.QChat.chat;
import static com.hr_handlers.chat.entity.QChatRoom.chatRoom;

@Repository
@AllArgsConstructor
public class ChatCustomRepositoryImpl implements ChatCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ChatResponseDto> findByEmployeeId(Long employeeId, String keyword, Pageable pageable) {

        BooleanExpression condition = (keyword != null && !keyword.trim().isEmpty())
                ? chat.chatRoom.title.containsIgnoreCase(keyword)
                : null; // 전체 조회

        List<ChatResponseDto> chatResponseDtos = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ChatResponseDto.class,
                                chat.employee.id,
                                chat.chatRoom.id,
                                chat.chatRoom.title,
                                chat.chatRoom.isSecret,
                                chat.employee.empNo,
                                chat.employee.name,
                                chat.employee.position,
                                chat.employee.department.deptName
                                )
                )
                .from(chat)
                .join(chat.chatRoom)
                .join(chat.employee)
                .where(
                        chat.employee.id.eq(employeeId)
                                .and(condition)
                )
                .orderBy(chat.chatRoom.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(chat.count())
                .from(chat)
                .where(
                        chat.employee.id.eq(employeeId)
                                .and(condition)
                )
                .fetchOne();

        return new PageImpl<>(chatResponseDtos, pageable, total);
    }

    @Override
    public Chat findByChatId(Long chatRoomId, Long employeeId) {
        return jpaQueryFactory
                .selectFrom(chat)
                .where(
                    chat.chatRoom.id.eq(chatRoomId)
                            .and(chat.employee.id.eq(employeeId))
                )
                .fetchOne();
    }

    @Transactional
    @Override
    public Chat insertChat(Long chatRoomId, Long employeeId) {
        Chat existingChat = findByChatId(chatRoomId, employeeId);

        if (existingChat == null) {
            String sql = "INSERT IGNORE INTO chat (chat_room_id, employee_id) VALUES (:chatRoomId, :employeeId)";
            entityManager.createNativeQuery(sql)
                    .setParameter("chatRoomId", chatRoomId)
                    .setParameter("employeeId", employeeId)
                    .executeUpdate();

            return findByChatId(chatRoomId, employeeId);
        } else {
            return existingChat;
        }
    }

    @Override
    public List<ChatInviteResponseDto> findEmployeesNotInChat(Long chatRoomId, String keyword) {
        QChat chat = QChat.chat;  // chat 객체 선언
        QEmployee employee = QEmployee.employee;
        QDepartment department = QDepartment.department; // Department 객체 선언

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                ChatInviteResponseDto.class,
                                employee.empNo,
                                employee.name,
                                employee.position,
                                employee.department.deptName
                        )
                )
                .from(employee)
                .leftJoin(employee.department, department)
                .where(
                        // Employee가 해당 채팅방에 참여하지 않은 경우
                        JPAExpressions.selectFrom(chat)
                                .where(
                                        chat.chatRoom.id.eq(chatRoomId)  // 해당 chatRoomId에 해당하는 채팅방
                                                .and(chat.employee.id.eq(employee.id))  // Employee가 해당 채팅방에 참여한 경우
                                )
                                .notExists(),

                        // 키워드 검색 조건
                        employee.name.likeIgnoreCase("%" + keyword + "%")
                                .or(department.deptName.likeIgnoreCase("%" + keyword + "%"))
                )
                .fetch();
    }

    @Override
    public List<ChatResponseDto> findJoinedEmployees(Long chatRoomId) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                ChatResponseDto.class,
                                chat.employee.id,
                                chat.chatRoom.id,
                                chat.chatRoom.title,
                                chat.chatRoom.isSecret,
                                chat.employee.empNo,
                                chat.employee.name,
                                chat.employee.position,
                                chat.employee.department.deptName
                        )
                )
                .from(chat)
                .join(chat.chatRoom)
                .join(chat.employee)
                .where(
                        chat.chatRoom.id.eq(chatRoomId)
                )
                .fetch();
    }

    // 채팅 참여 삭제
    @Override
    public void deleteChatByChatRoomId(Long chatRoomId) {
        jpaQueryFactory
                .delete(chat)
                .where(
                        chat.chatRoom.id.eq(chatRoomId)
                )
                .execute();
    }

    // 채팅 참여 인원 확인
    @Override
    public Long countChatByChatRoomId(Long chatRoomId) {
        return jpaQueryFactory
                .select(chat.count())
                .from(chat)
                .where(
                        chat.chatRoom.id.eq(chatRoomId)
                )
                .fetchOne();
    }
}
