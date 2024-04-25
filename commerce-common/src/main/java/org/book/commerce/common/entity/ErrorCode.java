package org.book.commerce.common.entity;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    BAD_REQUEST("400","Bad Request Error", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_RESPONSE("401","Unauthorized Error",HttpStatus.UNAUTHORIZED),
    INVALID_EMAIL("408","Send Failed Error",HttpStatus.CONFLICT);

    private String errorCode;
    private String errMsg;
    private HttpStatus status;

    ErrorCode(String errorCode, String errMsg, HttpStatus status) {
        this.errorCode = errorCode;
        this.errMsg = errMsg;
        this.status = status;
    }
}
