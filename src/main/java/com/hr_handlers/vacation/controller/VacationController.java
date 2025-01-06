package com.hr_handlers.vacation.controller;

import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.vacation.dto.*;
import com.hr_handlers.vacation.service.VacationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacation")
@RequiredArgsConstructor
@Tag(name = "휴가", description = "휴가 API")
public class VacationController {

    private final VacationService vacationService;

    // 휴가 상세 조회
    @GetMapping("/{vacationId}")
    @Operation(summary = "휴가 상세 조회", description = "로그인한 사원 본인의 휴가 1건에 대한 상세 조회")
    public SuccessResponse<VacationDetailResponseDto> getVacationDetail(@PathVariable("vacationId") Long id){
        return vacationService.getVacationDetail(id);
    }

    // 휴가 승인 대기 목록 조회
    @GetMapping("/pending")
    @Operation(summary = "승인 대기 휴가 목록 조회", description = "로그인한 사원 본인의 승인 대기 휴가 목록 조회")
    public SuccessResponse<List<PendingVacationResponseDto>> getPendingVacations(Authentication authentication){
        return vacationService.getPendingVacations(authentication.getName());
    }

    // 휴가 승인 확정 목록 조회
    @GetMapping("/approved")
    @Operation(summary = "승인 확정 휴가 목록 조회", description = "로그인한 사원 본인의 승인 확정 휴가 목록 조회")
    public SuccessResponse<Page<ApprovedVacationResponseDto>> getApprovedVacations(
            Authentication authentication,
            @RequestParam(defaultValue = "0", value = "page") int page,
            @RequestParam(defaultValue = "4", value = "size") int size
    ) {
        return vacationService.getApprovedVacations(authentication.getName(), PageRequest.of(page, size));
    }

    // 휴가 등록
    @PostMapping
    @Operation(summary = "휴가 신청", description = "사원 휴가 신청")
    public SuccessResponse<VacationResponseDto> enrollVacation(
            @RequestBody @Valid VacationRequestDto request,
            Authentication authentication
    ){
        return vacationService.enrollVacation(request, authentication.getName());
    }

    // 휴가 수정
    @PutMapping("/{vacationId}")
    @Operation(summary = "휴가 수정", description = "휴가 수정")
    public SuccessResponse<VacationResponseDto> modifyVacation(
            @PathVariable("vacationId") Long id,
            @RequestBody VacationRequestDto request
    ){
        return vacationService.modifyVacation(id, request);
    }

    // 휴가 삭제
    @DeleteMapping("/{vacationId}")
    @Operation(summary = "휴가 삭제", description = "휴가 삭제")
    public SuccessResponse<Boolean> deleteVacation(@PathVariable("vacationId") Long id){
        return vacationService.deleteVacation(id);
    }

    // 휴가 일수 정보 조회
    @GetMapping("/summary")
    @Operation(summary = "휴가 일수 정보 조회", description = "잔여 휴가, 승인 대기 휴가, 사용 휴가 일수")
    public SuccessResponse<VacationSummaryResponseDto> getBalance(Authentication authentication){
        return vacationService.getBalance(authentication.getName());
    }
}
