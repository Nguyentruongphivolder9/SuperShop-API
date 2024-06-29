package com.project.supershop.handler;

import java.util.Map;

public class UnprocessableException extends RuntimeException{
    public UnprocessableException(String message) {
        super(message);
    }

    public UnprocessableException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnprocessableException(Throwable cause) {
        super(cause);
    }
}
