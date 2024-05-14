package org.book.commerce.common.exception;

import org.book.commerce.common.entity.ErrorCode;
import org.springframework.http.HttpStatus;

public class CommonException extends RuntimeException{
    private ErrorCode errorCode;

    public CommonException(String message,ErrorCode errorCode){
        super(message);
        this.errorCode=errorCode;
    }


    public String getErrMsg(){
        return errorCode.getErrMsg();
    }

    public String getErrCode(){
        return errorCode.getErrorCode();
    }

    public HttpStatus getStatus(){
        return errorCode.getStatus();
    }
}
