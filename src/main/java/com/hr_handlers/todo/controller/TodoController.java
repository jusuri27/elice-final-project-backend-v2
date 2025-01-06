package com.hr_handlers.todo.controller;

import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.todo.dto.*;
import com.hr_handlers.todo.service.HolidayService;
import com.hr_handlers.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
@Tag(name = "일정", description = "일정 API")
public class TodoController {

    private final TodoService todoService;
    private final HolidayService holidayService;

    // 월별 일정 조회
    @GetMapping()
    @Operation(summary = "월별 일정 조회", description = "로그인한 사원 월별 일정 조회")
    public SuccessResponse<List<AllTodoResponseDto>> getAllTodo(
            Authentication authentication,
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ) {
        return todoService.getAllTodo(authentication.getName(), start, end);
    }

    // 월별 공휴일 조회
    @GetMapping("/holidays/{year}/{month}")
    @Operation(summary = "공휴일 조회", description = "전월, 익월, 해당월의 공휴일 조회")
    public SuccessResponse<List<HolidayResponseDto>> getHolidays(
            @PathVariable("year") int year,
            @PathVariable("month") int month
    ) {
        return holidayService.getHolidays(year, month);
    }

    @GetMapping("today")
    @Operation(summary = "오늘 일정 조회", description = "홈 화면 오늘의 일정 조회")
    public SuccessResponse<List<TodoTodayResponseDto>> getTodayTodo(Authentication authentication){
        return todoService.getTodayTodo(authentication.getName());
    }

    // 일정 상세 조회
    @GetMapping("/detail/{todoId}")
    @Operation(summary = "일정 상세 조회", description = "일정 상세 조회")
    public SuccessResponse<TodoResponseDto> getTodo(@PathVariable("todoId") Long id){
        return todoService.getTodo(id);
    }

    // 일정 등록
    @PostMapping
    @Operation(summary = "일정 등록", description = "일정 등록")
    public SuccessResponse<TodoResponseDto> enrollTodo(
            @Valid @RequestBody TodoRequestDto request,
            Authentication authentication
    ) {
        return todoService.enrollTodo(request, authentication.getName());
    }

    // 일정 수정
    @PutMapping("/{todoId}")
    @Operation(summary = "일정 수정", description = "일정 수정")
    public SuccessResponse<TodoResponseDto> modifyTodo(
            @PathVariable("todoId") Long id,
            @Valid @RequestBody TodoModifyRequestDto request
    ) {
        return todoService.modifyTodo(id, request);
    }

    // 일정 삭제
    @DeleteMapping("/{todoId}")
    @Operation(summary = "일정 삭제", description = "일정 삭제")
    public SuccessResponse<Boolean> deleteTodo(@PathVariable("todoId") Long id){
        return todoService.deleteTodo(id);
    }
}
