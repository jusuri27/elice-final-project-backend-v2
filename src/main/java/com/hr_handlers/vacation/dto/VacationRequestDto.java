package com.hr_handlers.vacation.dto;

import com.hr_handlers.vacation.entity.VacationType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VacationRequestDto {

    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;

    @NotNull(message = "휴가 유형을 선택해주세요.")
    private VacationType type;

    @NotNull(message = "시작 일자를 선택해주세요.")
    @Future(message = "시작일은 현재 시간 이후여야 합니다.")
    private Timestamp startDate;

    @NotNull(message = "종료 일자를 선택해주세요.")
    @Future(message = "종료일은 현재 시간 이후여야 합니다.")
    private Timestamp endDate;

    @NotEmpty(message = "휴가 사유를 입력해주세요")
    private String reason;

}
