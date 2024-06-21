package org.book.commerce.common.entity;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNAUTHORIZED_RESPONSE("401","Unauthorized Error",HttpStatus.UNAUTHORIZED),
    INVALID_EMAIL("409","Send Failed Error",HttpStatus.CONFLICT),
    SERVICE_UNAVAILABLE("503","Service is not available",HttpStatus.SERVICE_UNAVAILABLE);


    private String errorCode;
    private String errMsg;
    private HttpStatus status;

    ErrorCode(String errorCode, String errMsg, HttpStatus status) {
        this.errorCode = errorCode;
        this.errMsg = errMsg;
        this.status = status;
    }
}
