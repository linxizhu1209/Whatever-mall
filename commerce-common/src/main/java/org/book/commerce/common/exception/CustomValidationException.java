package org.book.commerce.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
public class CustomValidationException extends RuntimeException{

    public CustomValidationException(String message,Map<String,String> errors){
        super(message);
        this.message = message;
        this.errors = errors;
    }

    private String message;
    private Map<String,String> errors;

}
