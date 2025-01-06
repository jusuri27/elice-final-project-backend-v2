package com.hr_handlers.todo.repository;

import com.hr_handlers.todo.dto.AllTodoResponseDto;
import com.hr_handlers.todo.dto.TodoModifyRequestDto;
import com.hr_handlers.todo.dto.TodoResponseDto;
import com.hr_handlers.todo.dto.TodoTodayResponseDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface TodoCustomRepository {

    // 일정 전체 조회
    List<AllTodoResponseDto> findAllTodoByEmployeeId(
            String empNo,
            Timestamp start,
            Timestamp end);

    // 일정 삭제
    void deleteTodo(Long id);

    // 일정 수정
    TodoResponseDto modifyTodo(Long id, TodoModifyRequestDto request);

    List<TodoTodayResponseDto> findTodayTodosByEmployeeId(String empNo, LocalDateTime today);
}
