package com.user.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApplicationException extends RuntimeException{

    private int statusCode;

    public ApplicationException(String message,  int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

}
