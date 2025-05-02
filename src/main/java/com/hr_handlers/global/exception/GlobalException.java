package com.hr_handlers.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    ErrorCode errorCode;
}
