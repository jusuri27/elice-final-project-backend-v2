package com.hr_handlers.todo.repository;

import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import com.hr_handlers.todo.dto.AllTodoResponseDto;
import com.hr_handlers.todo.dto.TodoModifyRequestDto;
import com.hr_handlers.todo.dto.TodoResponseDto;
import com.hr_handlers.todo.dto.TodoTodayResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static com.hr_handlers.employee.entity.QEmployee.employee;
import static com.hr_handlers.todo.entity.QTodo.todo;

@Repository
@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    public List<AllTodoResponseDto> findAllTodoByEmployeeId(
            String empNo,
            Timestamp start,
            Timestamp end
    ) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                AllTodoResponseDto.class,
                                todo.id,
                                todo.title,
                                todo.startTime,
                                todo.endTime,
                                todo.employee.id
                        )
                )
                .from(todo)
                .where(
                        todo.employee.empNo.eq(empNo),
                        todo.startTime.goe(start),
                        todo.endTime.loe(end)
                )
                .fetch();
    }

    @Override
    public void deleteTodo(Long id) {
        long deletedCount = jpaQueryFactory
                .delete(todo)
                .where(todo.id.eq(id))
                .execute();

        if(deletedCount == 0){
            throw new GlobalException(ErrorCode.TODO_NOT_FOUND);
        }
    }

    @Override
    public TodoResponseDto modifyTodo(Long id, TodoModifyRequestDto request) {

        long updatedCount = jpaQueryFactory
                .update(todo)
                .where(todo.id.eq(id))
                .set(todo.title, request.getTitle())
                .set(todo.content, request.getContent())
                .set(todo.startTime, request.getStartTime())
                .set(todo.endTime, request.getEndTime())
                .execute();

        entityManager.flush();
        entityManager.clear();

        if(updatedCount == 0){
            throw new GlobalException(ErrorCode.TODO_NOT_FOUND);
        }

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                TodoResponseDto.class,
                                todo.id,
                                todo.title,
                                todo.content,
                                todo.startTime,
                                todo.endTime,
                                todo.employee.id
                        )
                )
                .from(todo)
                .where(todo.id.eq(id))
                .fetchOne();
    }

    @Override
    public List<TodoTodayResponseDto> findTodayTodosByEmployeeId(String empNo, LocalDateTime today) {
        LocalDateTime startOfDay = today.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = today.toLocalDate().atTime(23, 59, 59);

        return jpaQueryFactory
                .select(Projections.constructor(TodoTodayResponseDto.class,
                        todo.id,
                        todo.title,
                        todo.startTime,
                        todo.endTime
                ))
                .from(todo)
                .join(todo.employee, employee)
                .where(
                        employee.empNo.eq(empNo)
                                .and(
                                        todo.startTime.between(
                                                        Timestamp.valueOf(startOfDay),
                                                        Timestamp.valueOf(endOfDay)
                                                )
                                                .or(todo.endTime.between(
                                                        Timestamp.valueOf(startOfDay),
                                                        Timestamp.valueOf(endOfDay)
                                                ))
                                                .or(
                                                        todo.startTime.before(Timestamp.valueOf(startOfDay))
                                                                .and(todo.endTime.after(Timestamp.valueOf(endOfDay)))
                                                )
                                )
                )
                .orderBy(todo.startTime.asc())
                .fetch();
    }
}
