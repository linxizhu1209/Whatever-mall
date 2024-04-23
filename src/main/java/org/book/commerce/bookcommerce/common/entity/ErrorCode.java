package org.book.commerce.bookcommerce.common.entity;

import ch.qos.logback.core.spi.ErrorCodes;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

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
