package com.hr_handlers.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //휴가
    VACATION_NOT_FOUND(HttpStatus.NOT_FOUND, "VACATION-01", "해당 휴가를 조회할 수 없습니다."),
    INSUFFICIENT_LEAVE_BALANCE(HttpStatus.NOT_FOUND, "VACATION-02", "잔여 휴가 일수가 부족합니다."),

    //근태
    ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATTENDANCE-01", "해당 출근 기록을 찾을 수 없습니다."),

    //일정
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "TODO-01", "해당 일정을 찾을 수 없습니다."),
    HOLIDAY_API_ERROR(HttpStatus.BAD_REQUEST, "TODO-02", "잘못된 API 요청입니다."),
    HOLIDAY_PARSE_ERROR(HttpStatus.BAD_REQUEST, "TODO-03", "XML 파싱 에러가 발생했습니다."),

    //게시판
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST-01", "해당 게시글을 찾을 수 없습니다."),
    POSTS_NOT_FOUND(HttpStatus.NOT_FOUND, "POST-02", "전체 게시글을 조회할 수 없습니다."),
    POST_CREATE_FAILED(HttpStatus.NOT_FOUND, "POST-03", "게시글 생성에 실패했습니다."),
    POST_UPDATE_FAILED(HttpStatus.NOT_FOUND, "POST-04", "게시글 수정에 실패했습니다."),
    POST_DELETE_FAILED(HttpStatus.NOT_FOUND, "POST-05", "게시글 삭제에 실패했습니다."),

    // 댓글
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT-01", "댓글을 찾을 수 없습니다."),
    COMMENT_UPDATE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "COMMENT-02", "댓글 수정 권한이 없습니다."),
    COMMENT_DELETE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "COMMENT-03", "댓글 삭제 권한이 없습니다."),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT-04", "대댓글을 찾을 수 없습니다."),
    REPLY_UPDATE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "COMMENT-05", "대댓글 수정 권한이 없습니다."),
    REPLY_DELETE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "COMMENT-06", "대댓글 삭제 권한이 없습니다."),

    //채팅
    CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT-01", "참여한 채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_ROOM-01", "해당 채팅방을 찾을 수 없습니다."),
    CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_MESSAGE-01", "해당 메시지를 찾을 수 없습니다."),
    CHAT_MESSAGE_UPDATE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "CHAT_MESSAGE-02", "메시지 수정 권한이 없습니다."),
    CHAT_MESSAGE_DELETE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "CHAT_MESSAGE-03", "메시지 삭제 권한이 없습니다."),

    //급여
    SALARY_NOT_FOUND(HttpStatus.NOT_FOUND, "SALARY-01", "해당 급여를 조회할 수 없습니다."),

    // 엑셀
    EXCEL_HEADER_NOT_FOUND(HttpStatus.NOT_FOUND, "EXCEL--01", "엑셀 헤더 생성 중에 문제가 발생했습니다."),
    INVALID_TIME_PERIOD(HttpStatus.BAD_REQUEST, "EXCEL-02", "잘못된 기간입니다."),
    INVALID_DOWNLOAD_SCOPE(HttpStatus.BAD_REQUEST, "EXCEL-03", "잘못된 다운로드 범위입니다."),

    //사원
    EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "EMPLOYEE-01", "사원을 찾을 수 없습니다."),
    EMPLOYEE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"EMPLOYEE-02", "사원 번호가 이미 존재합니다."),
    EMPLOYEE_EMAIL_MISMATCH(HttpStatus.BAD_REQUEST, "EMPLOYEE-03", "사원번호와 이메일이 일치하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"EMPLOYEE-04", "현재 비밀번호가 일치하지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST,"EMPLOYEE-05", "새로운 비밀번호와 확인 비밀번호가 일치하지 않습니다."),

    //부서
    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DEPARTMENT-01", "부서를 찾을 수 없습니다."),
    TEAM_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"DEPARTMENT-02", "해당 사원이 속한 부서를 찾을 수 없습니다."),

    //로그인
    INVALID_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "LOGIN-01", "잘못된 로그인 요청입니다."),
    TEMP_PASSWORD_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMPLOYEE-02", "임시 비밀번호 생성 실패"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMPLOYEE-03", "메일 전송 실패"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "EMPLOYEE-04", "잘못된 이메일 형식입니다."),

    //JWT
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "JWT-01", "유효하지 않은 Access Token입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
