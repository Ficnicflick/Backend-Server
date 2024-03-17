package com.example.BackendServer.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseStatus {
    /** 성공 2xx */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다"),


    /** client error - 4xx */
    EXIST_USER(false, HttpStatus.CONFLICT.value(), "이미 존재하는 회원입니다"),
    NON_EXIST_USER(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다"),
    WRONG_PASSWORD(false, HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다."),
    WRONG_REQUEST_BODY(false, HttpStatus.BAD_REQUEST.value(), "입력 형식이 알맞지 않습니다."),
    EXIST_NICKNAME(false, HttpStatus.UNAUTHORIZED.value(), "이미 존재하는 닉네임입니다."),
    INVALID_TOKEN(false, HttpStatus.UNAUTHORIZED.value(),"Invalid JWT Token"),
    EXPRIED_TOKEN(false, HttpStatus.UNAUTHORIZED.value(), "Expired JWT Token"),
    UNSURPPORTED_TOKEN(false, HttpStatus.UNAUTHORIZED.value(),"Unsupported JWT Token"),
    EMPTY_TOKEN_CLAIM(false, HttpStatus.UNAUTHORIZED.value(),"JWT claims string is empty."),
    NOT_EXIST_REFRESHTOKEN(false, HttpStatus.NOT_FOUND.value(), "저장된 refreshToken이 존재하지 않습니다."),
    BLACK_TOKNE_REFRESHTOKEN(false, HttpStatus.UNAUTHORIZED.value(), "해당 토큰은 블랙 토큰입니다."),


    /** server error - 5xx */
    DATABASE_INSERT_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 저장에 실패하였습니다."),
    EXTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "외부 API 호출을 실패했습니다.")

    ;

    private final boolean isSuccess;
    private final int code;
    private final String message;

    /**
     *
     * @param isSuccess
     * @param code: Http Status Code
     * @param message: 설명
     */
    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

}
