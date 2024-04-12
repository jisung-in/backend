package com.jisungin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    BOOK_NOT_FOUND(404, "책을 찾을 수 없습니다."),
    BOOK_ALREADY_EXIST(400, "이미 등록된 책 정보 입니다."),
    BOOK_INVALID_INFO(400, "올바르지 않은 책 정보 입니다."),
    PARTICIPATION_CONDITION_ERROR(400, "참가 조건은 1개 이상이어야 합니다."),
    OAUTH_TYPE_NOT_FOUND(404, "지원하지 않는 소셜 로그인입니다."),
    TALK_ROOM_NOT_FOUND(400, "토크방을 찾을 수 없습니다."),
    UNAUTHORIZED_REQUEST(400, "권한이 없는 사용자입니다."),
    COMMENT_NOT_FOUND(404, "의견을 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(404, "리뷰를 찾을 수 없습니다."),
    TALK_ROOM_LIKE_NOT_FOUND(404, "토크방 좋아요를 찾을 수 없습니다."),
    LIKE_EXIST(400, "이미 좋아요를 눌렀습니다."),
    REQUEST_TIME_OUT(408, "요청 시간이 만료 되었습니다."),
    COMMENT_LIKE_NOT_FOUND(404, "의견 좋아요를 찾을 수 없습니다."),
    REVIEW_LIKE_NOT_FOUND(404, "리뷰 좋아요를 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(400, "파일이 없습니다."),
    S3_UPLOAD_FAIL(400, "이미지 업로드가 실패되었습니다."),
    NOT_IMAGE(400, "이미지 파일이 아닙니다."),
    UNABLE_WRITE_COMMENT(400, "의견을 쓸 권한이 없습니다.");

    private final int code;
    private final String message;

    ErrorCode(HttpStatus code, String message) {
        this.code = code.value();
        this.message = message;
    }

}
