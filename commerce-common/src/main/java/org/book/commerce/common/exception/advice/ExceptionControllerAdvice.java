package org.book.commerce.common.exception.advice;

import lombok.extern.slf4j.Slf4j;
import org.book.commerce.common.dto.ErrorResponse;
import org.book.commerce.common.exception.*;
import org.springframework.boot.context.config.ConfigDataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException nfe){
        log.error("Client 요청 이후 DB 검색 중 에러발생하여 다음처럼 출력합니다."+nfe.getMessage());
        return nfe.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotAcceptException.class)
    public String handleNotAcceptException(NotAcceptException nae){
        log.error("Client 요청이 다음의 이유로 거부됩니다"+nae.getMessage());
        return nae.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidValueException.class)
    public String handleInvalidValueException(InvalidValueException ive){
        log.error("Client 요청에 문제가 있어 다음처럼 출력합니다."+ive.getMessage());
        return ive.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity handleConflictException(ConflictException ce){
        log.error("Client 요청이 충돌되어 다음처럼 출력합니다"+ce.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ce.getMessage());
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity handleCommonException(CommonException e){
        ErrorResponse errRes = new ErrorResponse(e);
        return errRes.build();
    }
}
