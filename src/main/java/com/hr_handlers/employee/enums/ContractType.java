package com.hr_handlers.employee.enums;

import java.util.Arrays;

public enum ContractType {
    PERMANENT("정규직"),
    CONTRACT("계약직"),
    PART_TIME("시간제 정규직"),
    DISPATCH("파견직"),
    INDEFINITE("무기계약직"),
    INTERNSHIP("인턴십");

    private final String description;

    ContractType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ContractType fromDescription(String description) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid contract type: " + description));
    }
}