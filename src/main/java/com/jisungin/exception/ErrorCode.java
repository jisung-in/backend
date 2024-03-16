package com.jisungin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(400, "사용자를 찾을 수 없습니다."),
    BOOK_NOT_FOUND(400, "책을 찾을 수 없습니다."),
    BOOK_ALREADY_EXIST(400, "이미 등록된 책 정보 입니다."),
    PARTICIPATION_CONDITION_ERROR(400, "참가 조건은 1개 이상이어야 합니다.");

    private final int code;
    private final String message;

    ErrorCode(HttpStatus code, String message) {
        this.code = code.value();
        this.message = message;
    }

}
