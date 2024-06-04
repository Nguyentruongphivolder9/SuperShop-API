package com.project.supershop.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleException(NotFoundException ext){

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .message("Request to page not found")
                                .error(ext.getMessage())
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .build()
                );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        Map<String, String> errors = new HashMap<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    FieldError fe = (FieldError)error;
                    errors.put(fe.getField(), fe.getDefaultMessage());
                });

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                        ExceptionResponse.builder()
                                .message("Invalid format!")
                                .validationErrors(errors)
                                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        exp.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .message("Internal error, please contact the admin")
                                .error(exp.getMessage())
                                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .build()
                );
    }

}
