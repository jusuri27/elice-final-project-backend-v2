package com.hr_handlers.employee.enums;

public enum Role {
    ROLE_EMPLOYEE("사원"),
    ROLE_ADMIN("관리자(인사관리팀)");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}