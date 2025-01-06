package com.hr_handlers.salary.controller;


import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorResponse;
import com.hr_handlers.salary.dto.response.SalaryResponse;
import com.hr_handlers.salary.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/salary")
@RequiredArgsConstructor
@Tag(name = "사용자 급여관리", description = "사용자 급여관리 관련 API")
public class SalaryController {

    private final SalaryService salaryService;

    @Operation(summary = "로그인한 유저의 급여 전체 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "급여 조회 성공"),
            @ApiResponse(responseCode = "404", description = "급여를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping()
    public SuccessResponse<Page<SalaryResponse>> getUserSalary(
            @PageableDefault(page = 0, size = 15)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "salary.payDate", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "employee.position", direction = Sort.Direction.ASC)
            }) Pageable pageable,
            Authentication authentication
    )
    {
        return salaryService.getSalaryByUser(pageable, authentication.getName());
    }

    @Operation(summary = "로그인한 유저의 급여 다운로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "급여 다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "급여 엑셀 다운로드 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/excel/download")
    public SuccessResponse<Boolean> excelDownload(OutputStream stream, Authentication authentication) throws IOException, IllegalAccessException {
        return salaryService.excelDownloadSalary(stream, authentication.getName());
    }
}
