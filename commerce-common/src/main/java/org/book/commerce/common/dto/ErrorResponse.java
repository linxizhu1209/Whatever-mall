package org.book.commerce.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.book.commerce.common.exception.CommonException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private String code;
    private HttpStatus status;

    public ErrorResponse(Throwable e){
        CommonException ex = (CommonException)e;
        this.code = ex.getErrCode();
        this.status = ex.getStatus();
        this.message = ex.getMessage();
    }

    public ResponseEntity build(){
        ResponseEntity responseEntity = new ResponseEntity<>(this,status);
        return responseEntity;
    }
}
