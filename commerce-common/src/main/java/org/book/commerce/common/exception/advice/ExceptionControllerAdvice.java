package org.book.commerce.common.exception.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.common.dto.ErrorResponse;
import org.book.commerce.common.exception.*;
import org.springframework.boot.context.config.ConfigDataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionControllerAdvice {

    private final ObjectMapper objectMapper;
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
    public String handleConflictException(ConflictException ce){
        log.error("Client 요청이 충돌되어 다음처럼 출력합니다"+ce.getMessage());
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(ce.getMessage());
        return ce.getMessage();  // 이렇게 하는 거랑 위 로직이랑 똑같음.
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity handleCommonException(CommonException e){
        ErrorResponse errRes = new ErrorResponse(e);
        return errRes.build();
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(FeignException feignException) throws JsonProcessingException {
//        log.error("Feign 통신 과정 중 예외가 발생하여 다음 처럼 출력합니다"+fe.getMessage());
        log.error("[exception handler실행중]");
        String responseJson = feignException.contentUTF8();
        log.info("responseJson: "+responseJson);
//        Map<String, String> responseMap = objectMapper.readValue(responseJson,Map.class);
        return ResponseEntity.status(feignException.status()).body(responseJson);
    }
}
