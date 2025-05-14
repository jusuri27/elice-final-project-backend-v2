package com.hr_handlers.admin.controller;


import com.hr_handlers.admin.dto.salary.request.*;
import com.hr_handlers.admin.dto.salary.response.AdminSalaryResponseDto;
import com.hr_handlers.admin.service.AdminSalaryService;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorResponse;
import com.hr_handlers.global.utils.ExcelUploadUtils;
import com.hr_handlers.global.utils.ExcelUploadUtilsV2;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/admin/salary")
@RequiredArgsConstructor
@Tag(name = "어드민 급여관리", description = "어드민 급여관리 관련 API")
public class AdminSalaryController {

    private final AdminSalaryService adminSalaryService;
    private final ExcelUploadUtils excelUploadUtils;
    private final ExcelUploadUtilsV2 excelUploadUtilsV2;

    @Operation(summary = "급여 전체 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "급여 조회 성공"),
            @ApiResponse(responseCode = "404", description = "급여를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping()
    public SuccessResponse<List<AdminSalaryResponseDto>> getAllUserSalary() {
        return adminSalaryService.getAllUserSalary();
    }

    @Operation(summary = "급여 조건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "급여 조회 성공"),
            @ApiResponse(responseCode = "404", description = "급여를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("search")
    public SuccessResponse<Page<AdminSalaryResponseDto>> searchSalary(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "salary.payDate", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "employee.position", direction = Sort.Direction.ASC)
            }) Pageable pageable,
            @RequestBody @Validated AdminSalarySearchRequestDto adminSalarySearchRequestDto
    ) {
        return adminSalaryService.searchSalary(pageable, adminSalarySearchRequestDto);
    }

    @Operation(summary = "급여 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "급여 추가 성공"),
            @ApiResponse(responseCode = "404", description = "급여 추가 실패 또는 사원을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping()
    public SuccessResponse<Boolean> createSalary(@RequestBody @Validated AdminSalaryCreateRequestDto salaryCreateRequest) {
        return adminSalaryService.createSalary(salaryCreateRequest);
    }

    @Operation(summary = "급여 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "급여 수정 성공"),
            @ApiResponse(responseCode = "404", description = "급여 수정 실패 또는 급여를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping()
    public SuccessResponse<Boolean> updateSalary(@RequestBody @Validated AdminSalaryUpdateRequestDto adminSalaryUpdateRequestDto) {
        return adminSalaryService.updateSalary(adminSalaryUpdateRequestDto);
    }

    @Operation(summary = "급여 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "급여 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "급여 삭제 실패 또는 급여를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping()
    public SuccessResponse<Boolean> deleteSalary(@RequestBody List<Long> salaryIds) {
        return adminSalaryService.deleteSalary(salaryIds);
    }

    @Operation(summary = "급여 엑셀 업로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "급여 엑셀 업로드 성공"),
            @ApiResponse(responseCode = "404", description = "급여 엑셀 업로드 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/excel/upload")
    public SuccessResponse<Boolean> excelUpload(@RequestPart(value = "file", required = true) MultipartFile file) throws IOException, InterruptedException {
//        return adminSalaryService.excelUploadSalary(excelUploadUtils.parseExcelToObject(file, AdminSalaryExcelUploadRequestDto.class)); // DOM방식 엑셀 파싱
        return adminSalaryService.excelUploadSalary(excelUploadUtilsV2.parseExcelToObject(file, AdminSalaryExcelUploadRequestDto.class)); // SAX방식 엑셀 파싱
    }

    @Operation(summary = "급여 엑셀 다운로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "급여 엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "급여 엑셀 다운로드 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/excel/download")
    public SuccessResponse<Boolean> excelDownload(OutputStream stream, @RequestBody @Validated AdminSalaryExcelRequestDto adminSalaryExcelRequestDto) throws IOException, IllegalAccessException {
        return adminSalaryService.excelDownloadSalary(stream, adminSalaryExcelRequestDto);
    }
}
