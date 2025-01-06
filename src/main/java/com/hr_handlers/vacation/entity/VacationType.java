package com.hr_handlers.vacation.entity;

import lombok.Getter;

@Getter
public enum VacationType {
    ANNUAL, // 연차
    HALF(0.5), // 반차
    SICK, // 병가
    PUBLIC(0.0); // 공가

    private final Double fixedDays;

    VacationType() {
        this.fixedDays = null; // 시작/종료일로 계산해야 하는 경우
    }

    VacationType(Double fixedDays) {
        this.fixedDays = fixedDays; // 고정 일수가 있는 경우
    }
}
