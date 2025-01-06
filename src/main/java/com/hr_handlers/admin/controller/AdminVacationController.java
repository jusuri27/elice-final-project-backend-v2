package com.hr_handlers.admin.controller;

import com.hr_handlers.admin.dto.vacation.AdminVacationResponseDto;
import com.hr_handlers.admin.dto.vacation.AdminVacationStatusResponseDto;
import com.hr_handlers.admin.service.AdminVacationService;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.vacation.dto.VacationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vacation")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "어드민 휴가 관리", description = "어드민 휴가 관리 API")
public class AdminVacationController {

    private final AdminVacationService adminVacationService;

    @GetMapping
    @Operation(summary = "모든 휴가 승인 대기 목록 조회", description = "모든 휴가 승인 대기 목록 조회")
    public SuccessResponse<Page<AdminVacationResponseDto>> getPendingVacations(
            @RequestParam(defaultValue = "0", value = ("page")) int page,
            @RequestParam(defaultValue = "15", value = ("size")) int size
    ) {
        return adminVacationService.getPendingVacations(PageRequest.of(page, size));
    }

    @GetMapping("/status")
    @Operation(summary = "모든 사원 휴가 상태 조회", description = "연차,반차,병가,공가,총 사용일수,잔여일수 조회")
    public SuccessResponse<Page<AdminVacationStatusResponseDto>> getVacationStatus(
            @RequestParam(defaultValue = "0", value = ("page")) int page,
            @RequestParam(defaultValue = "15", value = ("size")) int size
    ) {
        return adminVacationService.getVacationStatus(PageRequest.of(page, size));
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "휴가 승인", description = "휴가 승인")
    public SuccessResponse<VacationResponseDto> approveVacation(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        return adminVacationService.approveVacation(id, authentication.getName());
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "휴가 반려", description = "휴가 반려")
    public SuccessResponse<VacationResponseDto> rejectVacation(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        return adminVacationService.rejectVacation(id, authentication.getName());
    }
}
